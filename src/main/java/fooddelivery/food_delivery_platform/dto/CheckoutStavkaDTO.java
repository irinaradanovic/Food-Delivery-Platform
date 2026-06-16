package fooddelivery.food_delivery_platform.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutStavkaDTO {
    private Long stavkaMenijaId;
    private Long proizvodId;
    private String naziv;
    private Integer kolicina;
    private BigDecimal jedinicnaCena;
    private BigDecimal ukupnaCena;
    private String napomena;
}
