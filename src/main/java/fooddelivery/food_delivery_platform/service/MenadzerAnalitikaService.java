package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.HronikaEksperimenataDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.PokretaciIzmenaDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.StabilnostUpravljanjaDTO;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenadzerAnalitikaService {

    @Autowired
    private MeniRepository meniRepository;

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
        String zakljucak = "Formiranje cena je stabilno. Izmene su izbalansirane sa uvođenjem novih artikala.";
        if (procenatCena > 50.0) {
            zakljucak = "Ako 'Izmena cenovnika' drastično dominira, to je signal menadžeru da cene variraju previše često i da nabavka/formiranje cena nije stabilno.";
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
}
