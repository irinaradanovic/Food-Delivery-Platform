package fooddelivery.food_delivery_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KorpaPreporukaResponseDTO {

    private List<PreporukaStavkaDTO> preporuke;

    private List<String> nedostajuciTipovi;

    private String poruka;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreporukaStavkaDTO {
        private Long stavkaId;
        private Long proizvodId;
        private String naziv;
        private String fotografija;
        private String kategorija;
        private String tipObroka;
        private BigDecimal cena;
        private double skorRelevantnosti;
        private String razlogPreporuke;
    }
}