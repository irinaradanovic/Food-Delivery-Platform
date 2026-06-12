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

    // Tipovi obroka i njihove ključne reči
    private static final Map<String, List<String>> TIP_KLJUCNE_RECI = Map.of(
            "predjelo",  List.of("salat", "predjelo", "čorb", "soup", "salad"),
            "glavno",    List.of("meso", "piletina", "riba", "chicken", "meat", "fish", "burger", "pizza", "pasta"),
            "desert",    List.of("desert", "kolač", "torta", "tiramisu", "cheesecake", "palačink"),
            "pice",      List.of("piće", "pića", "drink", "cola", "pepsi", "voda", "limunad", "sok", "kafa")
    );

    private static final Map<String, String> TIP_PORUKE = Map.of(
            "pice",   "Dodaj piće uz obrok! 🥤",
            "desert", "Završi obrok slatkim zalogajem! 🍰",
            "glavno", "Dodaj glavno jelo uz narudžbinu! 🍽️",
            "predjelo", "Počni sa predjelom! 🥗"
    );

    public KorpaPreporukaResponseDTO getPreporuke(KorpaPreporukaRequestDTO request) {
        int max = request.getMaxPreporuka() > 0 ? request.getMaxPreporuka() : 5;

        // Dohvati sve stavke menija koje su u korpi
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

        // ── Signal 1: Korelacije iz istorije porudžbina ───────────────────
        // Koje stavke se najčešće naručuju zajedno sa stavkama koje su u korpi?
        Map<Long, Integer> korelacije = izracunajKorelacije(
                request.getStavkeMenijaIds(), request.getKupacId()
        );
        for (StavkaMenija kandidat : kandidati) {
            int frekvencija = korelacije.getOrDefault(kandidat.getStavkaId(), 0);
            if (frekvencija > 0) {
                skorovi.merge(kandidat.getStavkaId(), frekvencija * 5.0, Double::sum);
                razlozi.putIfAbsent(kandidat.getStavkaId(), "Često se naručuje uz ovaj izbor");
            }
        }

        // ── Signal 2: Kombo dopuna — detektuj koji tipovi nedostaju ───────
        Set<String> tipUKorpi = detektujTipove(korpaStavke);
        Set<String> sviTipovi = new LinkedHashSet<>(List.of("glavno", "predjelo", "pice", "desert"));
        List<String> nedostajuciTipovi = sviTipovi.stream()
                .filter(t -> !tipUKorpi.contains(t))
                .collect(Collectors.toList());

        // Daj bonus kandidatima koji popunjavaju nedostajuće tipove
        for (StavkaMenija kandidat : kandidati) {
            for (String nedostajuciTip : nedostajuciTipovi) {
                if (odgovaraTipu(kandidat, nedostajuciTip)) {
                    // Piće i desert dobijaju veći bonus jer se češće zaboravljaju
                    double bonus = (nedostajuciTip.equals("pice") || nedostajuciTip.equals("desert")) ? 8.0 : 4.0;
                    skorovi.merge(kandidat.getStavkaId(), bonus, Double::sum);
                    razlozi.putIfAbsent(kandidat.getStavkaId(), "Dopunjava tvoj obrok");
                    break;
                }
            }
        }

        // ── Signal 3: Kategorijska afinost iz korpe ───────────────────────
        // Ako je u korpi pizza, predloži i druge popularne stavke iste kategorije
        Set<Long> kategorijeuKorpi = korpaStavke.stream()
                .filter(s -> s.getProizvod() != null && s.getProizvod().getKategorija() != null)
                .map(s -> s.getProizvod().getKategorija().getKategorijaId())
                .collect(Collectors.toSet());

        for (StavkaMenija kandidat : kandidati) {
            if (kandidat.getProizvod() == null || kandidat.getProizvod().getKategorija() == null) continue;
            Long katId = kandidat.getProizvod().getKategorija().getKategorijaId();
            if (kategorijeuKorpi.contains(katId)) {
                skorovi.merge(kandidat.getStavkaId(), 2.0, Double::sum);
                razlozi.putIfAbsent(kandidat.getStavkaId(), "Slično onome što si izabrao");
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
                            .tipObroka(odredinTip(s))
                            .cena(s.getCena())
                            .skorRelevantnosti(e.getValue())
                            .razlogPreporuke(razlozi.getOrDefault(e.getKey(), "Preporučujemo"))
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
                            .tipObroka(odredinTip(s))
                            .cena(s.getCena())
                            .skorRelevantnosti(0)
                            .razlogPreporuke("Možda te zanima")
                            .build())
                    .collect(Collectors.toList());
        }

        // Kreiraj poruku na osnovu prvog nedostajućeg tipa
        String poruka = nedostajuciTipovi.isEmpty() ? null
                : TIP_PORUKE.get(nedostajuciTipovi.get(0));

        return KorpaPreporukaResponseDTO.builder()
                .preporuke(preporuke)
                .nedostajuciTipovi(nedostajuciTipovi)
                .poruka(poruka)
                .build();
    }

    // Analizira istoriju svih porudžbina i broji koliko puta se svaka stavka
    // pojavljuje u istoj porudžbini sa stavkama iz korpe
    private Map<Long, Integer> izracunajKorelacije(List<Long> korpaStavkeMenijaIds, Long kupacId) {
        Map<Long, Integer> korelacije = new HashMap<>();
        if (korpaStavkeMenijaIds == null || korpaStavkeMenijaIds.isEmpty()) return korelacije;

        // Dohvati sve porudžbine (opciono filtrirano po kupcu za personalizovanije rezultate)
        List<Porudzbina> porudzbine = kupacId != null
                ? porudzbinaRepository.findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(kupacId)
                : porudzbinaRepository.findAll();

        for (Porudzbina p : porudzbine) {
            List<StavkaPorudzbine> stavke = stavkaPorudzbineRepository
                    .findByPorudzbinaPorudzbinaId(p.getPorudzbinaId());

            // ID-jevi stavki menija u ovoj porudžbini
            Set<Long> stavkeMenijaUPorudzbini = stavke.stream()
                    .filter(sp -> sp.getStavkaMenija() != null)
                    .map(sp -> sp.getStavkaMenija().getStavkaId())
                    .collect(Collectors.toSet());

            // Ako se porudžbina preklapa sa korpom, sve ostale stavke su korelacije
            boolean preklapanje = korpaStavkeMenijaIds.stream()
                    .anyMatch(stavkeMenijaUPorudzbini::contains);

            if (preklapanje) {
                stavkeMenijaUPorudzbini.stream()
                        .filter(id -> !korpaStavkeMenijaIds.contains(id))
                        .forEach(id -> korelacije.merge(id, 1, Integer::sum));
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

    private Set<String> detektujTipove(List<StavkaMenija> stavke) {
        Set<String> tipovi = new HashSet<>();
        for (StavkaMenija s : stavke) {
            for (String tip : TIP_KLJUCNE_RECI.keySet()) {
                if (odgovaraTipu(s, tip)) {
                    tipovi.add(tip);
                    break;
                }
            }
        }
        return tipovi;
    }

    private boolean odgovaraTipu(StavkaMenija s, String tip) {
        if (s.getProizvod() == null) return false;
        String naziv = Optional.ofNullable(s.getProizvod().getNaziv()).orElse("").toLowerCase();
        String kat = s.getProizvod().getKategorija() != null
                ? s.getProizvod().getKategorija().getNaziv().toLowerCase() : "";
        String tekst = naziv + " " + kat;
        return TIP_KLJUCNE_RECI.getOrDefault(tip, List.of()).stream().anyMatch(tekst::contains);
    }

    private String odredinTip(StavkaMenija s) {
        for (String tip : TIP_KLJUCNE_RECI.keySet()) {
            if (odgovaraTipu(s, tip)) return tip;
        }
        return "ostalo";
    }
}