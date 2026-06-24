package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.KomboAnalitikaDTO;
import fooddelivery.food_delivery_platform.model.PrikazaniKombo;
import fooddelivery.food_delivery_platform.repository.PrikazaniKomboRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KomboAnalitikaService {

    private final PrikazaniKomboRepository komboRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public KomboAnalitikaDTO izracunajZaKupca(Long kupacId, int dani) {
        LocalDateTime od = LocalDateTime.now().minusDays(dani);
        List<PrikazaniKombo> lista = komboRepository
                .findByKupac_KorisnikIdAndPrikazanoUAfterOrderByPrikazanoUDesc(kupacId, od);
        return izracunajIzListe(lista);
    }

    public KomboAnalitikaDTO izracunajZaSve(int dani) {
        LocalDateTime od = LocalDateTime.now().minusDays(dani);
        List<PrikazaniKombo> lista = komboRepository
                .findByPrikazanoUAfterOrderByPrikazanoUDesc(od);
        return izracunajIzListe(lista);
    }

    private KomboAnalitikaDTO izracunajIzListe(List<PrikazaniKombo> lista) {
        if (lista.isEmpty()) return prazna();

        int ukupno   = lista.size();
        int uspesnih = (int) lista.stream().filter(PrikazaniKombo::getUspesna).count();
        double stopa = zaokruzi((double) uspesnih / ukupno * 100);

        // Distribucija poklapanja (0 = nista naruceno, 1 = jedna stavka, itd.)
        Map<Integer, Long> dist = lista.stream()
                .collect(Collectors.groupingBy(
                        k -> k.getBrojNarucenihStavki() != null ? k.getBrojNarucenihStavki() : 0,
                        Collectors.counting()));

        List<KomboAnalitikaDTO.PoklapatjeStatDTO> distribucija = dist.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> KomboAnalitikaDTO.PoklapatjeStatDTO.builder()
                        .brojNarucenihStavki(e.getKey())
                        .brojKomboa((int)(long) e.getValue())
                        .udeo(zaokruzi((double) e.getValue() / ukupno * 100))
                        .build())
                .collect(Collectors.toList());

        // Detalji
        List<KomboAnalitikaDTO.KomboDetaljDTO> detalji = lista.stream()
                .map(k -> KomboAnalitikaDTO.KomboDetaljDTO.builder()
                        .komboId(k.getId())
                        .ukupnoStavki(k.getStavkeMenijaIds() != null ? k.getStavkeMenijaIds().size() : 0)
                        .narucenihStavki(k.getBrojNarucenihStavki() != null ? k.getBrojNarucenihStavki() : 0)
                        .prikazanoU(k.getPrikazanoU() != null ? k.getPrikazanoU().format(FMT) : "N/A")
                        .realizovanoU(k.getRealizovanoU() != null ? k.getRealizovanoU().format(FMT) : null)
                        .uspesna(Boolean.TRUE.equals(k.getUspesna()))
                        .build())
                .collect(Collectors.toList());

        return KomboAnalitikaDTO.builder()
                .ukupnoPrikazano(ukupno)
                .ukupnoUspesnih(uspesnih)
                .stopaUspesnosti(stopa)
                .distribucijaPoklapanja(distribucija)
                .detalji(detalji)
                .build();
    }

    private KomboAnalitikaDTO prazna() {
        return KomboAnalitikaDTO.builder()
                .ukupnoPrikazano(0).ukupnoUspesnih(0).stopaUspesnosti(0.0)
                .distribucijaPoklapanja(Collections.emptyList())
                .detalji(Collections.emptyList())
                .build();
    }

    private double zaokruzi(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}