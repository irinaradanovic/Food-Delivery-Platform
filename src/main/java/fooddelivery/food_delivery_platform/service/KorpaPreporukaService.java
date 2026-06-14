package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.KorpaPreporukaRequestDTO;
import fooddelivery.food_delivery_platform.dto.KorpaPreporukaResponseDTO;
import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KorpaPreporukaService {

    private final StavkaMenijaRepository stavkaMenijaRepository;
    private final MeniRepository meniRepository;
    private final PorudzbinaRepository porudzbinaRepository;
    private final StavkaPorudzbineRepository stavkaPorudzbineRepository;

    private static final List<TipObroka> PRIORITET_TIPOVA =
            List.of(TipObroka.PICE, TipObroka.DESERT, TipObroka.GLAVNO, TipObroka.PREDJELO);


    private static final Map<TipObroka, Double> TIP_BONUS = Map.of(
            TipObroka.PICE,     8.0,
            TipObroka.DESERT,   8.0,
            TipObroka.GLAVNO,   4.0,
            TipObroka.PREDJELO, 4.0,
            TipObroka.OSTALO,   1.0
    );

    public KorpaPreporukaResponseDTO getPreporuke(KorpaPreporukaRequestDTO request) {
        int max = request.getMaxPreporuka() > 0 ? request.getMaxPreporuka() : 5;

        // Dohvati stavke menija koje su u korpi
        List<StavkaMenija> korpaStavke = new ArrayList<>();
        if (request.getStavkeMenijaIds() != null) {
            for (Long id : request.getStavkeMenijaIds()) {
                stavkaMenijaRepository.findById(id).ifPresent(korpaStavke::add);
            }
        }

        // Dohvati sve dostupne stavke iz aktivnih menija restorana
        List<StavkaMenija> sveDostupne = dohvatiAktivneStavke(request.getRestoranId());

        // Ukloni iz kandidata ono što je već u korpi
        Set<Long> uKorpiIds = korpaStavke.stream()
                .map(StavkaMenija::getStavkaId).collect(Collectors.toSet());
        List<StavkaMenija> kandidati = sveDostupne.stream()
                .filter(s -> !uKorpiIds.contains(s.getStavkaId()))
                .collect(Collectors.toList());

        // Skor mapa: stavkaId -> skor
        Map<Long, Double> skorovi = new HashMap<>();
        Map<Long, String> razlozi = new HashMap<>();


        Set<TipObroka> tipUKorpi = detektujTipove(korpaStavke);
        List<TipObroka> nedostajuciTipovi = PRIORITET_TIPOVA.stream()
                .filter(t -> !tipUKorpi.contains(t))
                .collect(Collectors.toList());

        for (StavkaMenija kandidat : kandidati) {
            TipObroka tipKandidata = odredinTip(kandidat);
            if (nedostajuciTipovi.contains(tipKandidata)) {
                double bonus = TIP_BONUS.getOrDefault(tipKandidata, 1.0);
                skorovi.merge(kandidat.getStavkaId(), bonus, Double::sum);
                razlozi.putIfAbsent(kandidat.getStavkaId(), null);
            }
        }

        // Sortiraj po skoru i uzmi top N
        Map<Long, StavkaMenija> kandidatiMap = kandidati.stream()
                .collect(Collectors.toMap(StavkaMenija::getStavkaId, s -> s));

        List<KorpaPreporukaResponseDTO.PreporukaStavkaDTO> preporuke = skorovi.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(max)
                .map(e -> {
                    StavkaMenija s = kandidatiMap.get(e.getKey());
                    if (s == null || s.getProizvod() == null) return null;
                    return KorpaPreporukaResponseDTO.PreporukaStavkaDTO.builder()
                            .stavkaId(s.getStavkaId())
                            .proizvodId(s.getProizvod().getProizvodId())
                            .naziv(s.getProizvod().getNaziv())
                            .fotografija(s.getProizvod().getFotografija())
                            .kategorija(s.getProizvod().getKategorija() != null
                                    ? s.getProizvod().getKategorija().getNaziv() : "")
                            .tipObroka(odredinTip(s).name().toLowerCase())
                            .cena(s.getCena())
                            .skorRelevantnosti(e.getValue())
                            .razlogPreporuke(razlozi.getOrDefault(e.getKey(), null))
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Ako nema skorovanih preporuka, vrati random dostupne stavke
        if (preporuke.isEmpty() && !kandidati.isEmpty()) {
            preporuke = kandidati.stream()
                    .limit(max)
                    .filter(s -> s.getProizvod() != null)
                    .map(s -> KorpaPreporukaResponseDTO.PreporukaStavkaDTO.builder()
                            .stavkaId(s.getStavkaId())
                            .proizvodId(s.getProizvod().getProizvodId())
                            .naziv(s.getProizvod().getNaziv())
                            .fotografija(s.getProizvod().getFotografija())
                            .kategorija(s.getProizvod().getKategorija() != null
                                    ? s.getProizvod().getKategorija().getNaziv() : "")
                            .tipObroka(odredinTip(s).name().toLowerCase())
                            .cena(s.getCena())
                            .skorRelevantnosti(0)
                            .razlogPreporuke("Možda te zanima")
                            .build())
                    .collect(Collectors.toList());
        }

        String poruka = null;

        // Vrati nazive nedostajućih tipova kao stringove (za frontend kompatibilnost)
        List<String> nedostajuciTipoviStr = nedostajuciTipovi.stream()
                .map(t -> t.name().toLowerCase())
                .collect(Collectors.toList());

        return KorpaPreporukaResponseDTO.builder()
                .preporuke(preporuke)
                .nedostajuciTipovi(nedostajuciTipoviStr)
                .poruka(poruka)
                .build();
    }


    private Map<Long, Integer> izracunajKorelacije(List<Long> korpaStavkeMenijaIds, Long kupacId) {
        Map<Long, Integer> korelacije = new HashMap<>();
        if (korpaStavkeMenijaIds == null || korpaStavkeMenijaIds.isEmpty()) return korelacije;

        // Pretvori korpa stavkeMenijaIds u set proizvodIds za pouzdano poređenje
        Set<Long> korpaProizvodIds = korpaStavkeMenijaIds.stream()
                .map(id -> stavkaMenijaRepository.findById(id)
                        .map(sm -> sm.getProizvod() != null ? sm.getProizvod().getProizvodId() : null)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Porudzbina> porudzbine = kupacId != null
                ? porudzbinaRepository.findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(kupacId)
                : porudzbinaRepository.findAll();

        for (Porudzbina p : porudzbine) {
            List<StavkaPorudzbine> stavke = stavkaPorudzbineRepository
                    .findByPorudzbinaPorudzbinaId(p.getPorudzbinaId());

            // Grupišemo po proizvodId — isto kao što grupišemo korpu
            Map<Long, Long> stavkaToProizvod = stavke.stream()
                    .filter(sp -> sp.getStavkaMenija() != null
                            && sp.getStavkaMenija().getProizvod() != null)
                    .collect(Collectors.toMap(
                            sp -> sp.getStavkaMenija().getStavkaId(),
                            sp -> sp.getStavkaMenija().getProizvod().getProizvodId(),
                            (a, b) -> a  // duplikati stavkaId su nemogući, ali radi sigurnosti
                    ));

            Set<Long> proizvodiUPorudzbini = new HashSet<>(stavkaToProizvod.values());

            boolean preklapanje = proizvodiUPorudzbini.stream()
                    .anyMatch(korpaProizvodIds::contains);

            if (preklapanje) {
                proizvodiUPorudzbini.stream()
                        .filter(pid -> !korpaProizvodIds.contains(pid))
                        .forEach(pid -> korelacije.merge(pid, 1, Integer::sum));
            }
        }
        return korelacije;
    }

    private List<StavkaMenija> dohvatiAktivneStavke(Long restoranId) {
        return meniRepository.findByRestoranRestoranIdAndAktivanTrue(restoranId).stream()
                .flatMap(m -> stavkaMenijaRepository.findByMeniMeniIdAndObrisanFalse(m.getMeniId()).stream())
                .filter(StavkaMenija::isDostupno)
                .collect(Collectors.toList());
    }


    private Set<TipObroka> detektujTipove(List<StavkaMenija> stavke) {
        return stavke.stream()
                .map(this::odredinTip)
                .filter(t -> t != TipObroka.OSTALO)
                .collect(Collectors.toSet());
    }

    private TipObroka odredinTip(StavkaMenija s) {
        if (s.getProizvod() == null) return TipObroka.OSTALO;
        Kategorija kat = s.getProizvod().getKategorija();
        // Primarni izvor: enum polje na kategoriji
        if (kat != null && kat.getTipObroka() != null && kat.getTipObroka() != TipObroka.OSTALO) {
            return kat.getTipObroka();
        }

        String naziv = Optional.ofNullable(s.getProizvod().getNaziv()).orElse("").toLowerCase();
        String katNaziv = kat != null ? kat.getNaziv().toLowerCase() : "";
        String tekst = naziv + " " + katNaziv;
        if (tekst.contains("piće") || tekst.contains("pića") || tekst.contains("drink")
                || tekst.contains("cola") || tekst.contains("pepsi") || tekst.contains("voda")
                || tekst.contains("sok") || tekst.contains("kafa") || tekst.contains("limunad")) {
            return TipObroka.PICE;
        }
        if (tekst.contains("desert") || tekst.contains("kolač") || tekst.contains("torta")
                || tekst.contains("tiramisu") || tekst.contains("cheesecake")
                || tekst.contains("palačink")) {
            return TipObroka.DESERT;
        }
        if (tekst.contains("salat") || tekst.contains("predjelo") || tekst.contains("čorb")
                || tekst.contains("soup")) {
            return TipObroka.PREDJELO;
        }
        if (tekst.contains("meso") || tekst.contains("piletina") || tekst.contains("riba")
                || tekst.contains("chicken") || tekst.contains("burger") || tekst.contains("pizza")
                || tekst.contains("pasta") || tekst.contains("meat") || tekst.contains("fish")) {
            return TipObroka.GLAVNO;
        }
        return TipObroka.OSTALO;
    }
}