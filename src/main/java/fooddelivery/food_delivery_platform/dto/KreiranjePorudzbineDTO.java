package fooddelivery.food_delivery_platform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class KreiranjePorudzbineDTO {

    @NotNull
    private Long kupacId;

    @NotBlank
    private String adresaDostave;

    private String kuponKod;

    private String napomena;


    @NotEmpty
    private List<@Valid StavkaPorudzbineDTO> stavke;
}