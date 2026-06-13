package fooddelivery.food_delivery_platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class KorpaPreporukaRequestDTO {
    private Long restoranId;
    private Long kupacId;
    // ID-jevi stavki menija koje su trenutno u korpi
    private List<Long> stavkeMenijaIds;
    private int maxPreporuka; // default 5
}