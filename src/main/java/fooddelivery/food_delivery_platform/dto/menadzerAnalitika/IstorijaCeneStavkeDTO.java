package fooddelivery.food_delivery_platform.dto.menadzerAnalitika;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class IstorijaCeneStavkeDTO {
    private String verzija;
    private BigDecimal cena;
}
