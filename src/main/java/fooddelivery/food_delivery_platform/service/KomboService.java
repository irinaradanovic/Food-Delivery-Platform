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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KomboService {

    private final MeniRepository meniRepository;
    private final StavkaMenijaRepository stavkaMenijaRepository;

    public List<KomboResultDTO> predloziKomboe(KomboRequestDTO request) {
        if (request.getBrojStavki() < 1 || request.getBrojStavki() > 8) {
            return Collections.emptyList();
        }

        int maxRezultata = request.getMaxRezultata() > 0 ? request.getMaxRezultata() : 5;

        List<StavkaMenija> sveStavke = dohvatiStavkeAktivnihMenija(request.getRestoranId());

        List<Long> trazeneKategorije = request.getKategorijaIds() != null && !request.getKategorijaIds().isEmpty()
                ? request.getKategorijaIds()
                : null;

        // Grupiši stavke po kategorijaId — tačan match, bez keyword guessinga
        Map<Long, List<StavkaMenija>> poKategoriji = new HashMap<>();
        if (trazeneKategorije != null) {
            for (Long katId : trazeneKategorije) {
                List<StavkaMenija> kandidati = sveStavke.stream()
                        .filter(s -> s.getProizvod() != null
                                && s.getProizvod().getKategorija() != null
                                && katId.equals(s.getProizvod().getKategorija().getKategorijaId()))
                        .filter(s -> s.getCena() != null && s.getCena().compareTo(request.getMaxUkupnaCena()) <= 0)
                        .collect(Collectors.toList());
                poKategoriji.put(katId, kandidati);
            }
        }

        List<List<StavkaMenija>> sveKombinacije = new ArrayList<>();

        if (trazeneKategorije != null) {
            generisiKombinacije(trazeneKategorije, poKategoriji, new ArrayList<>(),
                    request.getBrojStavki(), sveStavke, request.getMaxUkupnaCena(),
                    sveKombinacije, maxRezultata * 3);
        } else {
            generisiBezKategorije(sveStavke, request.getBrojStavki(),
                    request.getMaxUkupnaCena(), sveKombinacije, maxRezultata * 3);
        }

        // Filtriraj po kalorijama i alergenima
        if (request.getMaxUkupneKalorije() != null || request.getMinUkupneKalorije() != null
                || (request.getIskljuciAlergeneIds() != null && !request.getIskljuciAlergeneIds().isEmpty())) {
            sveKombinacije.removeIf(combo -> {
                if (sadrzIskljuceneAlergene(combo, request.getIskljuciAlergeneIds())) return true;
                double zbirKalorija = sumaKalorija(combo);
                if (request.getMinUkupneKalorije() != null && zbirKalorija < request.getMinUkupneKalorije()) return true;
                if (request.getMaxUkupneKalorije() != null && zbirKalorija > request.getMaxUkupneKalorije()) return true;
                return false;
            });
        }

        sveKombinacije.sort((a, b) -> {
            int cmpBroj = Integer.compare(b.size(), a.size());
            if (cmpBroj != 0) return cmpBroj;
            return sumaKombinacije(a).compareTo(sumaKombinacije(b));
        });

        List<List<StavkaMenija>> deduplikovane = ukloniDuplikate(sveKombinacije);

        return deduplikovane.stream()
                .limit(maxRezultata)
                .map(combo -> pretvoriUDTO(combo, request.getMaxUkupnaCena()))
                .collect(Collectors.toList());
    }

    private void generisiKombinacije(List<Long> kategorije, Map<Long, List<StavkaMenija>> poKategoriji,
                                     List<StavkaMenija> trenutna, int ciljniBroj,
                                     List<StavkaMenija> sveStavke, BigDecimal maxCena,
                                     List<List<StavkaMenija>> rezultati, int maxBroj) {
        if (rezultati.size() >= maxBroj) return;

        int index = trenutna.size();

        if (index == kategorije.size()) {
            if (trenutna.size() < ciljniBroj) {
                BigDecimal preostalo = maxCena.subtract(sumaKombinacije(trenutna));
                Set<Long> korisceneId = trenutna.stream().map(StavkaMenija::getStavkaId).collect(Collectors.toSet());
                List<StavkaMenija> dopune = sveStavke.stream()
                        .filter(s -> !korisceneId.contains(s.getStavkaId()))
                        .filter(s -> s.getCena() != null && s.getCena().compareTo(preostalo) <= 0)
                        .limit(ciljniBroj - trenutna.size())
                        .collect(Collectors.toList());
                List<StavkaMenija> kompletna = new ArrayList<>(trenutna);
                kompletna.addAll(dopune);
                if (kompletna.size() == ciljniBroj) rezultati.add(kompletna);
            } else {
                rezultati.add(new ArrayList<>(trenutna));
            }
            return;
        }

        Long katId = kategorije.get(index);
        List<StavkaMenija> kandidati = poKategoriji.getOrDefault(katId, Collections.emptyList());

        BigDecimal dostupno = maxCena.subtract(sumaKombinacije(trenutna));
        Set<Long> korisceneId = trenutna.stream().map(StavkaMenija::getStavkaId).collect(Collectors.toSet());

        for (StavkaMenija kandidat : kandidati) {
            if (rezultati.size() >= maxBroj) break;
            if (korisceneId.contains(kandidat.getStavkaId())) continue;
            if (kandidat.getCena().compareTo(dostupno) > 0) continue;
            trenutna.add(kandidat);
            generisiKombinacije(kategorije, poKategoriji, trenutna, ciljniBroj, sveStavke, maxCena, rezultati, maxBroj);
            trenutna.remove(trenutna.size() - 1);
        }
    }

    private void generisiBezKategorije(List<StavkaMenija> stavke, int ciljniBroj,
                                       BigDecimal maxCena, List<List<StavkaMenija>> rezultati, int maxBroj) {
        generisiRekurzivno(stavke, 0, new ArrayList<>(), ciljniBroj,
                BigDecimal.ZERO, maxCena, rezultati, maxBroj);
    }

    private void generisiRekurzivno(List<StavkaMenija> stavke, int startIdx,
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
            generisiRekurzivno(stavke, i + 1, trenutna, ciljniBroj, novaCena, maxCena, rezultati, maxBroj);
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

    private BigDecimal sumaKombinacije(List<StavkaMenija> stavke) {
        return stavke.stream()
                .map(s -> s.getCena() != null ? s.getCena() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double sumaKalorija(List<StavkaMenija> stavke) {
        return stavke.stream()
                .mapToDouble(s -> {
                    if (s.getProizvod() == null || s.getProizvod().getKalorije() == null) return 0.0;
                    return s.getProizvod().getKalorije().doubleValue();
                }).sum();
    }

    private boolean sadrzIskljuceneAlergene(List<StavkaMenija> stavke, List<Long> iskljuceniIds) {
        if (iskljuceniIds == null || iskljuceniIds.isEmpty()) return false;
        return stavke.stream().anyMatch(s -> {
            if (s.getProizvod() == null || s.getProizvod().getAlergeni() == null) return false;
            return s.getProizvod().getAlergeni().stream()
                    .anyMatch(a -> iskljuceniIds.contains(a.getAlergenId()));
        });
    }

    private List<List<StavkaMenija>> ukloniDuplikate(List<List<StavkaMenija>> kombinacije) {
        Set<Set<Long>> vidjeni = new HashSet<>();
        List<List<StavkaMenija>> rezultat = new ArrayList<>();
        for (List<StavkaMenija> kombo : kombinacije) {
            Set<Long> kljuc = kombo.stream().map(StavkaMenija::getStavkaId).collect(Collectors.toSet());
            if (vidjeni.add(kljuc)) rezultat.add(kombo);
        }
        return rezultat;
    }

    private KomboResultDTO pretvoriUDTO(List<StavkaMenija> stavke, BigDecimal maxCena) {
        List<KomboResultDTO.KomboStavkaDTO> stavkeDto = stavke.stream().map(s -> {
            String kategorijaNaziv = (s.getProizvod() != null && s.getProizvod().getKategorija() != null)
                    ? s.getProizvod().getKategorija().getNaziv() : "";
            return KomboResultDTO.KomboStavkaDTO.builder()
                    .stavkaId(s.getStavkaId())
                    .nazivProizvoda(s.getProizvod() != null ? s.getProizvod().getNaziv() : "")
                    .opisProizvoda(s.getProizvod() != null ? s.getProizvod().getOpis() : "")
                    .fotografija(s.getProizvod() != null ? s.getProizvod().getFotografija() : null)
                    .kategorija(kategorijaNaziv)
                    .tipObroka(kategorijaNaziv)
                    .cena(s.getCena())
                    .build();
        }).collect(Collectors.toList());

        return KomboResultDTO.builder()
                .stavke(stavkeDto)
                .ukupnaCena(sumaKombinacije(stavke))
                .build();
    }
}