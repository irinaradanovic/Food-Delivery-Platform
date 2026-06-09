package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.KomboRequestDTO;
import fooddelivery.food_delivery_platform.dto.KomboResultDTO;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.model.Meni;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KomboService {

    private final MeniRepository meniRepository;
    private final StavkaMenijaRepository stavkaMenijaRepository;

    // Mapa ključnih reči po tipu obroka (mora biti usklađena sa frontendom)
    private static final Map<String, List<String>> TIP_KLJUCNE_RECI = Map.of(
            "predjelo",  List.of("salat", "predjelo", "čorb", "soup", "salad"),
            "glavno",    List.of("meso", "piletina", "riba", "chicken", "meat", "fish", "pork", "beef"),
            "desert",    List.of("desert", "kolač", "torta", "tiramisu", "cheesecake", "palačink", "dessert"),
            "pice",      List.of("piće", "pića", "drink", "cola", "pepsi", "voda", "limunad", "sok", "kafa"),
            "brza",      List.of("burger", "pizza", "nugget", "wings", "krilca", "wrap"),
            "pasta",     List.of("pasta", "tjestenina", "špagete", "pene", "carbonara", "bolognese")
    );

    public List<KomboResultDTO> predloziKomboe(KomboRequestDTO request) {
        if (request.getBrojStavki() < 1 || request.getBrojStavki() > 8) {
            return Collections.emptyList();
        }

        int maxRezultata = request.getMaxRezultata() > 0 ? request.getMaxRezultata() : 5;

        // Dohvati sve stavke iz aktivnih menija restorana
        List<StavkaMenija> sveStvake = dohvatiStavkeAktivnihMenija(request.getRestoranId());

        // Grupiši stavke po tipu obroka
        // Ako su tipovi zadani, provjeri da svaki tip ima kandidate
        List<String> trazeniTipovi = request.getTipovi() != null && !request.getTipovi().isEmpty()
                ? request.getTipovi()
                : null;

        // Ako su tipovi zadani i broj stavki != broj tipova —
        // popuni ostatak sa bilo kojom stavkom koja stane u budžet
        Map<String, List<StavkaMenija>> poTipu = new HashMap<>();

        if (trazeniTipovi != null) {
            for (String tip : trazeniTipovi) {
                List<StavkaMenija> kandidati = sveStvake.stream()
                        .filter(s -> odgovaraTipu(s, tip))
                        .filter(s -> s.getCena() != null && s.getCena().compareTo(request.getMaxUkupnaCena()) <= 0)
                        .collect(Collectors.toList());
                poTipu.put(tip, kandidati);
            }
        } else {
            // Bez tipova — sve stavke su kandidati za svaki slot
            poTipu.put("bilo", sveStvake.stream()
                    .filter(s -> s.getCena() != null)
                    .collect(Collectors.toList()));
        }

        // Generiši kombinacije rekurzivno
        List<List<StavkaMenija>> sveKombinacije = new ArrayList<>();

        if (trazeniTipovi != null) {
            generirajKombinacije(trazeniTipovi, poTipu, new ArrayList<>(),
                    request.getBrojStavki(), sveStvake, request.getMaxUkupnaCena(),
                    sveKombinacije, maxRezultata * 3);
        } else {
            generirajBezTipa(sveStvake, request.getBrojStavki(),
                    request.getMaxUkupnaCena(), sveKombinacije, maxRezultata * 3);
        }

        // Filtriraj po zbirnim kalorijama i alergenima
        if (request.getMaxUkupneKalorije() != null || request.getMinUkupneKalorije() != null
                || (request.getIskljuciAlergeneIds() != null && !request.getIskljuciAlergeneIds().isEmpty())) {
            sveKombinacije.removeIf(combo -> {
                // Provjera alergena
                if (sadrzIskljuceneAlergene(combo, request.getIskljuciAlergeneIds())) return true;
                // Provjera kalorija
                double zbirKalorija = sumaKalorija(combo);
                if (request.getMinUkupneKalorije() != null && zbirKalorija < request.getMinUkupneKalorije()) return true;
                if (request.getMaxUkupneKalorije() != null && zbirKalorija > request.getMaxUkupneKalorije()) return true;
                return false;
            });
        }

        // Sortiraj: najpre po broju stavki DESC, onda po ceni ASC (više za manje novca)
        sveKombinacije.sort((a, b) -> {
            int cmpBroj = Integer.compare(b.size(), a.size());
            if (cmpBroj != 0) return cmpBroj;
            BigDecimal sumaA = sumaKombinacije(a);
            BigDecimal sumaB = sumaKombinacije(b);
            return sumaA.compareTo(sumaB);
        });

        // Deduplikuj (ukloni kombinacije s istim setom stavki u drugom redoslijedu)
        List<List<StavkaMenija>> deduplikovane = deduplikuj(sveKombinacije);

        // Pretvori u DTO i vrati top N
        return deduplikovane.stream()
                .limit(maxRezultata)
                .map(combo -> pretvoriUDTO(combo, request.getMaxUkupnaCena()))
                .collect(Collectors.toList());
    }

    // Rekurzivno generisanje kombinacija sa tipovima
    private void generirajKombinacije(List<String> tipovi, Map<String, List<StavkaMenija>> poTipu,
                                      List<StavkaMenija> trenutna, int ciljniBroj,
                                      List<StavkaMenija> sveStvake, BigDecimal maxCena,
                                      List<List<StavkaMenija>> rezultati, int maxBroj) {
        if (rezultati.size() >= maxBroj) return;

        int index = trenutna.size();

        // Ako smo popunili sve tražene tipove
        if (index == tipovi.size()) {
            // Ako treba još stavki, dopuni random stavkama koje stanu
            if (trenutna.size() < ciljniBroj) {
                BigDecimal preostalo = maxCena.subtract(sumaKombinacije(trenutna));
                Set<Long> korisceneId = trenutna.stream()
                        .map(StavkaMenija::getStavkaId).collect(Collectors.toSet());
                List<StavkaMenija> dopune = sveStvake.stream()
                        .filter(s -> !korisceneId.contains(s.getStavkaId()))
                        .filter(s -> s.getCena() != null && s.getCena().compareTo(preostalo) <= 0)
                        .limit(ciljniBroj - trenutna.size())
                        .collect(Collectors.toList());
                List<StavkaMenija> kompletna = new ArrayList<>(trenutna);
                kompletna.addAll(dopune);
                if (kompletna.size() == ciljniBroj) {
                    rezultati.add(new ArrayList<>(kompletna));
                }
            } else {
                rezultati.add(new ArrayList<>(trenutna));
            }
            return;
        }

        String tip = tipovi.get(index);
        List<StavkaMenija> kandidati = poTipu.getOrDefault(tip, Collections.emptyList());

        BigDecimal potrosenoDoSad = sumaKombinacije(trenutna);
        // Koliko tipova ostaje nakon ovog?
        int preostalihSlotova = tipovi.size() - index - 1;
        // Ostavi malo prostora za preostale slotove (procena min cene)
        BigDecimal dostupno = maxCena.subtract(potrosenoDoSad);

        Set<Long> korisceneId = trenutna.stream()
                .map(StavkaMenija::getStavkaId).collect(Collectors.toSet());

        for (StavkaMenija kandidat : kandidati) {
            if (rezultati.size() >= maxBroj) break;
            if (korisceneId.contains(kandidat.getStavkaId())) continue;
            if (kandidat.getCena().compareTo(dostupno) > 0) continue;

            trenutna.add(kandidat);
            generirajKombinacije(tipovi, poTipu, trenutna, ciljniBroj, sveStvake,
                    maxCena, rezultati, maxBroj);
            trenutna.remove(trenutna.size() - 1);
        }
    }

    // Generisanje kombinacija bez tipova (bilo koje stavke)
    private void generirajBezTipa(List<StavkaMenija> stavke, int ciljniBroj,
                                  BigDecimal maxCena, List<List<StavkaMenija>> rezultati, int maxBroj) {
        generirajBezTipaRekurzivno(stavke, 0, new ArrayList<>(), ciljniBroj,
                BigDecimal.ZERO, maxCena, rezultati, maxBroj);
    }

    private void generirajBezTipaRekurzivno(List<StavkaMenija> stavke, int startIdx,
                                            List<StavkaMenija> trenutna, int ciljniBroj,
                                            BigDecimal trenutnaCena, BigDecimal maxCena,
                                            List<List<StavkaMenija>> rezultati, int maxBroj) {
        if (rezultati.size() >= maxBroj) return;

        if (trenutna.size() == ciljniBroj) {
            rezultati.add(new ArrayList<>(trenutna));
            return;
        }

        for (int i = startIdx; i < stavke.size(); i++) {
            if (rezultati.size() >= maxBroj) break;
            StavkaMenija s = stavke.get(i);
            if (s.getCena() == null) continue;
            BigDecimal novaCena = trenutnaCena.add(s.getCena());
            if (novaCena.compareTo(maxCena) > 0) continue;

            trenutna.add(s);
            generirajBezTipaRekurzivno(stavke, i + 1, trenutna, ciljniBroj,
                    novaCena, maxCena, rezultati, maxBroj);
            trenutna.remove(trenutna.size() - 1);
        }
    }

    private List<StavkaMenija> dohvatiStavkeAktivnihMenija(Long restoranId) {
        List<Meni> aktivniMeniji = meniRepository.findByRestoranRestoranIdAndAktivanTrue(restoranId);
        return aktivniMeniji.stream()
                .flatMap(m -> stavkaMenijaRepository.findByMeniMeniIdAndObrisanFalse(m.getMeniId()).stream())
                .filter(StavkaMenija::isDostupno)
                .collect(Collectors.toList());
    }

    private boolean odgovaraTipu(StavkaMenija stavka, String tip) {
        if (stavka.getProizvod() == null) return false;
        String naziv = (stavka.getProizvod().getNaziv() != null
                ? stavka.getProizvod().getNaziv() : "").toLowerCase();
        String katNaziv = (stavka.getProizvod().getKategorija() != null
                ? stavka.getProizvod().getKategorija().getNaziv() : "").toLowerCase();
        String tekst = naziv + " " + katNaziv;
        List<String> kljucneReci = TIP_KLJUCNE_RECI.getOrDefault(tip, Collections.emptyList());
        return kljucneReci.stream().anyMatch(tekst::contains);
    }

    private BigDecimal sumaKombinacije(List<StavkaMenija> stavke) {
        return stavke.stream()
                .map(s -> s.getCena() != null ? s.getCena() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<List<StavkaMenija>> deduplikuj(List<List<StavkaMenija>> kombinacije) {
        Set<Set<Long>> vidjeni = new HashSet<>();
        List<List<StavkaMenija>> rezultat = new ArrayList<>();
        for (List<StavkaMenija> kombo : kombinacije) {
            Set<Long> kljuc = kombo.stream()
                    .map(StavkaMenija::getStavkaId).collect(Collectors.toSet());
            if (vidjeni.add(kljuc)) {
                rezultat.add(kombo);
            }
        }
        return rezultat;
    }


    // Suma kalorija kombinacije (null kalorije se tretiraju kao 0)
    private double sumaKalorija(List<StavkaMenija> stavke) {
        return stavke.stream()
                .mapToDouble(s -> {
                    if (s.getProizvod() == null || s.getProizvod().getKalorije() == null) return 0.0;
                    return s.getProizvod().getKalorije().doubleValue();
                })
                .sum();
    }

    // Provjera da li kombinacija sadrži neki od isključenih alergena
    private boolean sadrzIskljuceneAlergene(List<StavkaMenija> stavke, List<Long> iskljuceniIds) {
        if (iskljuceniIds == null || iskljuceniIds.isEmpty()) return false;
        return stavke.stream().anyMatch(s -> {
            if (s.getProizvod() == null || s.getProizvod().getAlergeni() == null) return false;
            return s.getProizvod().getAlergeni().stream()
                    .anyMatch(a -> iskljuceniIds.contains(a.getAlergenId()));
        });
    }

    // Provjera da li jedna stavka sadrži isključene alergene
    private boolean stavkaSadrzIskljuceneAlergene(StavkaMenija s, List<Long> iskljuceniIds) {
        if (iskljuceniIds == null || iskljuceniIds.isEmpty()) return false;
        if (s.getProizvod() == null || s.getProizvod().getAlergeni() == null) return false;
        return s.getProizvod().getAlergeni().stream()
                .anyMatch(a -> iskljuceniIds.contains(a.getAlergenId()));
    }

    private KomboResultDTO pretvoriUDTO(List<StavkaMenija> stavke, BigDecimal maxCena) {
        BigDecimal ukupno = sumaKombinacije(stavke);

        List<KomboResultDTO.KomboStavkaDTO> stavkeDTo = stavke.stream().map(s -> {
            String tip = odredinTip(s);
            return KomboResultDTO.KomboStavkaDTO.builder()
                    .stavkaId(s.getStavkaId())
                    .nazivProizvoda(s.getProizvod() != null ? s.getProizvod().getNaziv() : "")
                    .opisProizvoda(s.getProizvod() != null ? s.getProizvod().getOpis() : "")
                    .fotografija(s.getProizvod() != null ? s.getProizvod().getFotografija() : null)
                    .kategorija(s.getProizvod() != null && s.getProizvod().getKategorija() != null
                            ? s.getProizvod().getKategorija().getNaziv() : "")
                    .tipObroka(tip)
                    .cena(s.getCena())
                    .build();
        }).collect(Collectors.toList());

        return KomboResultDTO.builder()
                .stavke(stavkeDTo)
                .ukupnaCena(ukupno)
                .build();
    }

    private String odredinTip(StavkaMenija s) {
        for (Map.Entry<String, List<String>> entry : TIP_KLJUCNE_RECI.entrySet()) {
            if (odgovaraTipu(s, entry.getKey())) return entry.getKey();
        }
        return "ostalo";
    }
}