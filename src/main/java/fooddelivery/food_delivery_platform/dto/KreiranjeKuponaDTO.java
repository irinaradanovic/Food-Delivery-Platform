package fooddelivery.food_delivery_platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class KreiranjeKuponaDTO {

    @NotNull
    private Long kupacId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal vrednost;

    @NotNull
    private LocalDateTime vaziDo;
}
