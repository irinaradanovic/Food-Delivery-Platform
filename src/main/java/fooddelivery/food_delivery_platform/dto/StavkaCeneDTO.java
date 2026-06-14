package fooddelivery.food_delivery_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StavkaCeneDTO {
    private Long stavkaId;
    private BigDecimal novaCena;
}
