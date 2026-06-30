package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.NacinPlacanja;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutPreviewDTO {

    @NotNull
    private Long kupacId;

    private Long kuponId;

    private String kuponKod;

    @NotNull
    private NacinPlacanja nacinPlacanja;

    private BigDecimal iznosKarticom;

    @NotEmpty
    private List<@Valid StavkaPorudzbineDTO> stavke;
}

