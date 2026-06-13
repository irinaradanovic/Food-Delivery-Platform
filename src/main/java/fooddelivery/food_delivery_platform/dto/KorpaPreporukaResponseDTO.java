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

    // Preporučene stavke menija
    private List<PreporukaStavkaDTO> preporuke;

    // Koji tipovi obroka nedostaju u korpi (za kombo dopunu)
    private List<String> nedostajuciTipovi;

    // Poruka korisniku (npr. "Dodaj piće za kompletan obrok!")
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
        private double skorRelevantnosti; // interni skor za debug
        private String razlogPreporuke;   // "Često se naručuje uz X", "Dopuna obroka" itd.
    }
}