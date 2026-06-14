package fooddelivery.food_delivery_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenovnikMasovniUpdateDTO {
    private List<StavkaCeneDTO> izmeneCena;
}
