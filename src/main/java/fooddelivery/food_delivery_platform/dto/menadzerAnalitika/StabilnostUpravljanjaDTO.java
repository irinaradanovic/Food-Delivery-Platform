package fooddelivery.food_delivery_platform.dto.menadzerAnalitika;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StabilnostUpravljanjaDTO {
    private Long grupniMeniId;
    private long ukupanBrojVerzija;
    private long brojRollbackova;
    private double rollbackRate; // (Error Rate)
    private boolean izbaciUpozorenjeBrzopletosti;
}
