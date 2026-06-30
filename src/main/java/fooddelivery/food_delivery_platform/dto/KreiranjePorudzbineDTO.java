package fooddelivery.food_delivery_platform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import fooddelivery.food_delivery_platform.model.NacinPlacanja;

import java.math.BigDecimal;
import java.util.List;

@Data
public class KreiranjePorudzbineDTO {

    @NotNull
    private Long kupacId;

    @NotBlank
    private String adresaDostave;

    private Long kuponId;

    private String kuponKod;

    private String napomena;

    @NotNull
    private NacinPlacanja nacinPlacanja;

    private BigDecimal iznosKarticom;

    @NotEmpty
    private List<@Valid StavkaPorudzbineDTO> stavke;
}

