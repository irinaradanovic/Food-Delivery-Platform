package fooddelivery.food_delivery_platform.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class KuponDTO {
    private Long kuponId;
    private String kod;
    private BigDecimal vrednost;
    private LocalDateTime vaziOd;
    private LocalDateTime vaziDo;
    private boolean aktivan;
    private boolean iskoriscen;
    private Long kupacId;
    private String kupacIme;
    private String kupacEmail;
}
