package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.*;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.PorudzbinaRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenadzerAnalitikaService {

    @Autowired
    private MeniRepository meniRepository;

    @Autowired
    private StavkaMenijaRepository stavkaMenijaRepository;

    @Autowired
    private PorudzbinaRepository porudzbinaRepository;

    // PRONALAZI STA NAJVISE UTICE NA KREIRANJE NOVE VERZIJE
    public PokretaciIzmenaDTO getPokretaceIzmena(Long grupniMeniId) {
        List<Meni> sveVerzije = meniRepository.findByGrupniMeniIdOrderByMeniIdDesc(grupniMeniId);
        if (sveVerzije.isEmpty()) return new PokretaciIzmenaDTO(grupniMeniId, 0, Map.of(), Map.of(), "Nema podataka.");

        long ukupanBrojVerzija = sveVerzije.size();
        Map<String, Long> brojPoRazlogu = new HashMap<>();

        for (Meni m : sveVerzije) {
            String razlog = m.getRazlogVerzionisanja() != null ? m.getRazlogVerzionisanja().name() : "NEPOZNATO";
            brojPoRazlogu.put(razlog, brojPoRazlogu.getOrDefault(razlog, 0L) + 1);
        }

        Map<String, Double> procenatPoRazlogu = new HashMap<>();
        brojPoRazlogu.forEach((razlog, broj) -> {
            double procenat = ((double) broj / ukupanBrojVerzija) * 100;
            procenatPoRazlogu.put(razlog, Math.round(procenat * 10.0) / 10.0);
        });

        double procenatCena = procenatPoRazlogu.getOrDefault("IZMENA_CENOVNIKA", 0.0);
        String zakljucak = "Formiranje cena je stabilno. Izmene su izbalansirane sa uvodjenjem novih artikala.";
        if (procenatCena > 50.0) {
            zakljucak = "Ako 'Izmena cenovnika' drasticno dominira, to je signal menadzeru da cene variraju previse cesto i da nabavka/formiranje cena nije stabilno.";
        }

        return new PokretaciIzmenaDTO(grupniMeniId, ukupanBrojVerzija, brojPoRazlogu, procenatPoRazlogu, zakljucak);
    }

    public StabilnostUpravljanjaDTO getStabilnostUpravljanja(Long grupniMeniId) {
        List<Meni> sveVerzije = meniRepository.findByGrupniMeniIdOrderByMeniIdDesc(grupniMeniId);
        if (sveVerzije.isEmpty()) return new StabilnostUpravljanjaDTO(grupniMeniId, 0, 0, 0.0, false);

        long ukupanBrojVerzija = sveVerzije.size();
        long brojRollbackova = sveVerzije.stream()
                .filter(m -> m.getRazlogVerzionisanja() != null && "ROLLBACK".equals(m.getRazlogVerzionisanja().name()))
                .count();

        double rollbackRate = ((double) brojRollbackova / ukupanBrojVerzija) * 100;
        rollbackRate = Math.round(rollbackRate * 10.0) / 10.0;
        boolean izbaciUpozorenje = rollbackRate > 20.0;

        return new StabilnostUpravljanjaDTO(grupniMeniId, ukupanBrojVerzija, brojRollbackova, rollbackRate, izbaciUpozorenje);
    }

    public HronikaEksperimenataDTO getHronikuEksperimenata(Long grupniMeniId) {
        List<Meni> sveVerzije = meniRepository.findByGrupniMeniIdOrderByMeniIdDesc(grupniMeniId);

        for (int i = 0; i < sveVerzije.size(); i++) {
            Meni trenutni = sveVerzije.get(i);

            if (trenutni.getRazlogVerzionisanja() != null && "ROLLBACK".equals(trenutni.getRazlogVerzionisanja().name())) {
                if (i + 1 < sveVerzije.size()) {
                    Meni ponistenaVerzija = sveVerzije.get(i + 1);

                    if (ponistenaVerzija.getDatumOd() != null && ponistenaVerzija.getDatumDo() != null) {
                        long daniAktivnosti = ChronoUnit.DAYS.between(ponistenaVerzija.getDatumOd(), ponistenaVerzija.getDatumDo());

                        String vremenskiProzor = daniAktivnosti == 0 ? "manje od 24 sata" : daniAktivnosti + " dana";

                        String uvid = String.format(
                                "Verzija %s (%s) je bila aktivna %s pre nego što je menadžer uradio rollback na verziju %s (koja je sada ponovo primenjena kroz verziju %s).",
                                ponistenaVerzija.getVerzija(),
                                ponistenaVerzija.getRazlogVerzionisanja(),
                                vremenskiProzor,
                                meniRepository.findMeniByMeniId(trenutni.getIzvornaVerzijaId()).getVerzija(),
                                trenutni.getVerzija()
                        );
                        return new HronikaEksperimenataDTO(grupniMeniId, uvid, true);
                    }
                }
            }
        }

        return new HronikaEksperimenataDTO(grupniMeniId, "Nema registrovanih kriznih rollback događaja.", false);
    }




    public KomparacijaVerzijaDTO komparacijaVerzija(Long meniIdA, Long meniIdB) {
        MeniStatistikaDTO statsA = generisiStatistikuZaMeni(meniIdA);
        MeniStatistikaDTO statsB = generisiStatistikuZaMeni(meniIdB);
        return new KomparacijaVerzijaDTO(statsA, statsB);
    }

    private MeniStatistikaDTO generisiStatistikuZaMeni(Long meniId) {
        Meni meni = meniRepository.findById(meniId)
                .orElseThrow(() -> new IllegalArgumentException("Meni sa ID-jem " + meniId + " ne postoji."));

        long brojStavki = stavkaMenijaRepository.countByMeniMeniIdAndObrisanFalse(meniId);
        long brojKategorija = stavkaMenijaRepository.countJedinstveneKategorijeZaMeni(meniId);
        double prosecnaCena = stavkaMenijaRepository.getProsecnaCenaZaMeni(meniId);
        prosecnaCena = Math.round(prosecnaCena * 100.0) / 100.0;

        long ukupanBrojPorudzbina = porudzbinaRepository.countUkupnoPorudzbinaZaMeni(meniId);
        long brojPotvrdjenih = porudzbinaRepository.countPotvrdjenihPorudzbinaZaMeni(meniId);
        BigDecimal ukupnaZarada = porudzbinaRepository.sumZaradaOdStavkiMenija(meniId);

        String razlog = meni.getRazlogVerzionisanja() != null ? meni.getRazlogVerzionisanja().name() : "NEPOZNATO";

        return new MeniStatistikaDTO(
                meni.getVerzija(),
                razlog,
                meni.getDatumOd(),
                meni.getDatumDo(),
                brojStavki,
                brojKategorija,
                prosecnaCena,
                ukupanBrojPorudzbina,
                brojPotvrdjenih,
                ukupnaZarada
        );
    }
}
