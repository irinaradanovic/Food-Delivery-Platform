package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.NacinPlacanja;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CheckoutSummaryDTO {
    private List<CheckoutStavkaDTO> stavke;
    private BigDecimal cenaArtikala;
    private BigDecimal cenaDostave;
    private BigDecimal popustArtikli;
    private BigDecimal popustDostava;
    private BigDecimal ukupnaCena;
    private NacinPlacanja nacinPlacanja;
    private BigDecimal iznosKarticom;
    private BigDecimal iznosKes;
    private String kuponKod;
    private String poruka;
}
