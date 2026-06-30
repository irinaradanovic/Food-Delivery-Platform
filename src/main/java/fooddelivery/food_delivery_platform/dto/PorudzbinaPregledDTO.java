package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.NacinPlacanja;
import fooddelivery.food_delivery_platform.model.StatusPlacanja;
import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PorudzbinaPregledDTO {
    private Long porudzbinaId;
    private Long kupacId;
    private String adresaDostave;
    private LocalDateTime datumKreiranja;
    private String napomena;
    private StatusPorudzbine status;
    private NacinPlacanja nacinPlacanja;
    private StatusPlacanja statusPlacanja;
    private BigDecimal cenaArtikala;
    private BigDecimal cenaDostave;
    private BigDecimal popustArtikli;
    private BigDecimal popustDostava;
    private BigDecimal iznosKarticom;
    private BigDecimal iznosKes;
    private BigDecimal ukupnaCena;
    private Long kuponId;
    private String kuponKod;
    private Long dostavljacId;
    private boolean imaRacun;
    private boolean imaStorno;
    private List<CheckoutStavkaDTO> stavke;
}

