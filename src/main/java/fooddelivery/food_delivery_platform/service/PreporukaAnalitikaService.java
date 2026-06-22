package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.PreporukaAnalitikaDTO;
import fooddelivery.food_delivery_platform.dto.TipPreporukeStatDTO;
import fooddelivery.food_delivery_platform.dto.KategorijaPreporukeStatDTO;
import fooddelivery.food_delivery_platform.dto.PreporukaDetaljDTO;
import fooddelivery.food_delivery_platform.model.PrikazanaPreporuka;
import fooddelivery.food_delivery_platform.repository.PrikazanaPreporukaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreporukaAnalitikaService {

    private final PrikazanaPreporukaRepository prikazanaRepo;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Čita direktno iz prikazane_preporuke — tačno šta je prikazano i
     * da li je kupac to naručio. Nema rekonstrukcije, nema pretpostavki.
     */
    public PreporukaAnalitikaDTO izracunajAnalitiku(Long kupacId, int dani) {

        LocalDateTime od = LocalDateTime.now().minusDays(dani);

        // Sve preporuke kupca u periodu (i uspešne i neuspešne)
        List<PrikazanaPreporuka> sve = prikazanaRepo
                .findByKupac_KorisnikIdAndPrikazanoUAfterOrderByPrikazanoUDesc(kupacId, od);

        if (sve.isEmpty()) {
            return praznaAnalitika();
        }

        // ── Razdvajanje po tipu ────────────────────────────────────────────
        Map<PrikazanaPreporuka.TipPreporuke, List<PrikazanaPreporuka>> poTipu =
                sve.stream().collect(Collectors.groupingBy(PrikazanaPreporuka::getTipPreporuke));

        TipPreporukeStatDTO personalizovane =
                izracunajTip(poTipu.get(PrikazanaPreporuka.TipPreporuke.PERSONALIZOVANA));
        TipPreporukeStatDTO trend =
                izracunajTip(poTipu.get(PrikazanaPreporuka.TipPreporuke.TREND));
        TipPreporukeStatDTO sezonske =
                izracunajTip(poTipu.get(PrikazanaPreporuka.TipPreporuke.SEZONSKA));
        TipPreporukeStatDTO vremenske =
                izracunajTip(poTipu.get(PrikazanaPreporuka.TipPreporuke.VREMENSKA));

        int ukupnoPrikazano = sve.size();
        long ukupnoUspesnih = sve.stream().filter(PrikazanaPreporuka::getUspesna).count();
        double ukupnaStopa  = zaokruzi((double) ukupnoUspesnih / ukupnoPrikazano * 100);

        // ── Po kategoriji ──────────────────────────────────────────────────
        Map<String, int[]> katMap = new LinkedHashMap<>();
        for (PrikazanaPreporuka p : sve) {
            if (p.getProizvod() == null) continue;
            String kat = p.getProizvod().getKategorija() != null
                    ? p.getProizvod().getKategorija().getNaziv() : "Ostalo";
            int[] stat = katMap.computeIfAbsent(kat, k -> new int[]{0, 0});
            stat[0]++;
            if (Boolean.TRUE.equals(p.getUspesna())) stat[1]++;
        }

        List<KategorijaPreporukeStatDTO> poKategoriji = katMap.entrySet().stream()
                .map(e -> {
                    double stopa = e.getValue()[0] > 0
                            ? zaokruzi((double) e.getValue()[1] / e.getValue()[0] * 100) : 0.0;
                    return KategorijaPreporukeStatDTO.builder()
                            .kategorija(e.getKey())
                            .prikazano(e.getValue()[0])
                            .uspesnih(e.getValue()[1])
                            .stopa(stopa)
                            .build();
                })
                .sorted(Comparator.comparingDouble(KategorijaPreporukeStatDTO::getStopa).reversed())
                .collect(Collectors.toList());

        // ── Detalji — svaka preporuka kao red ─────────────────────────────
        List<PreporukaDetaljDTO> detalji = sve.stream()
                .map(p -> {
                    String naziv = p.getProizvod() != null ? p.getProizvod().getNaziv() : "N/A";
                    String kat   = (p.getProizvod() != null && p.getProizvod().getKategorija() != null)
                            ? p.getProizvod().getKategorija().getNaziv() : "N/A";
                    return PreporukaDetaljDTO.builder()
                            .proizvodId(p.getProizvod() != null ? p.getProizvod().getProizvodId() : null)
                            .nazivProizvoda(naziv)
                            .kategorija(kat)
                            .tipPreporuke(p.getTipPreporuke().name())
                            .prikazanoU(p.getPrikazanoU() != null ? p.getPrikazanoU().format(FMT) : "N/A")
                            .realizovanoU(p.getRealizovanoU() != null ? p.getRealizovanoU().format(FMT) : null)
                            .uspesna(Boolean.TRUE.equals(p.getUspesna()))
                            .build();
                })
                .collect(Collectors.toList());

        return PreporukaAnalitikaDTO.builder()
                .ukupnoPrikazano(ukupnoPrikazano)
                .ukupnoUspesnih((int) ukupnoUspesnih)
                .ukupnaStopa(ukupnaStopa)
                .personalizovane(personalizovane)
                .trend(trend)
                .sezonske(sezonske)
                .vremenske(vremenske)
                .poKategoriji(poKategoriji)
                .detalji(detalji)
                .build();
    }

    private TipPreporukeStatDTO izracunajTip(List<PrikazanaPreporuka> lista) {
        if (lista == null || lista.isEmpty()) {
            return TipPreporukeStatDTO.builder()
                    .prikazano(0).uspesnih(0).stopa(0.0).build();
        }
        int prikazano  = lista.size();
        int uspesnih   = (int) lista.stream().filter(PrikazanaPreporuka::getUspesna).count();
        double stopa   = zaokruzi((double) uspesnih / prikazano * 100);
        return TipPreporukeStatDTO.builder()
                .prikazano(prikazano).uspesnih(uspesnih).stopa(stopa).build();
    }

    private PreporukaAnalitikaDTO praznaAnalitika() {
        TipPreporukeStatDTO prazan = TipPreporukeStatDTO.builder()
                .prikazano(0).uspesnih(0).stopa(0.0).build();
        return PreporukaAnalitikaDTO.builder()
                .ukupnoPrikazano(0).ukupnoUspesnih(0).ukupnaStopa(0.0)
                .personalizovane(prazan).trend(prazan).sezonske(prazan).vremenske(prazan)
                .poKategoriji(Collections.emptyList())
                .detalji(Collections.emptyList())
                .build();
    }

    private double zaokruzi(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}