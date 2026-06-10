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
public class KomboResultDTO {

    private List<KomboStavkaDTO> stavke;
    private BigDecimal ukupnaCena;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KomboStavkaDTO {
        private Long stavkaId;
        private String nazivProizvoda;
        private String opisProizvoda;
        private String fotografija;
        private String kategorija;
        private String tipObroka;
        private BigDecimal cena;
    }
}