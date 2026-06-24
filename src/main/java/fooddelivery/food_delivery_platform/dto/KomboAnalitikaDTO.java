package fooddelivery.food_delivery_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KomboAnalitikaDTO {

    private int    ukupnoPrikazano;       // ukupno prikazanih kombo setova
    private int    ukupnoUspesnih;        // bar jedna stavka naručena
    private double stopaUspesnosti;       // uspesnih / prikazano * 100

    // Distribucija — koliko komboa je imalo 1, 2, 3... naručenih stavki
    private List<PoklapatjeStatDTO> distribucijaPoklapanja;

    // Detalji po kombu
    private List<KomboDetaljDTO> detalji;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PoklapatjeStatDTO {
        private int brojNarucenihStavki;  // 0, 1, 2, 3...
        private int brojKomboa;
        private double udeo;              // % od ukupno prikazanih
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class KomboDetaljDTO {
        private Long   komboId;
        private int    ukupnoStavki;
        private int    narucenihStavki;
        private String prikazanoU;
        private String realizovanoU;
        private boolean uspesna;
    }
}