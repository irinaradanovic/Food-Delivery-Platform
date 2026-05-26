package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PromenaStatusaPorudzbineDTO {

    @NotNull
    private StatusPorudzbine noviStatus;
}