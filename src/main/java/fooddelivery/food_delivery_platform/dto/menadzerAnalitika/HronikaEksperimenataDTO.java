package fooddelivery.food_delivery_platform.dto.menadzerAnalitika;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HronikaEksperimenataDTO {
    private Long grupniMeniId;
    private String tekstUvida;
    private boolean imaZabelezenih;
}
