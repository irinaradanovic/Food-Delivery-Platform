package fooddelivery.food_delivery_platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StavkaPorudzbineDTO {

    @NotNull
    private Long stavkaMenijaId;

    @NotNull
    @Min(1)
    private Integer kolicina;

    private String napomena;
}
