package fooddelivery.food_delivery_platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class KorpaPreporukaRequestDTO {
    private Long restoranId;
    private Long kupacId;
    private List<Long> stavkeMenijaIds;
    private int maxPreporuka;
}