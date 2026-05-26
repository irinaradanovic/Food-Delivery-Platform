package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromenaStatusaPorudzbineResponseDTO {

    private Long porudzbinaId;
    private StatusPorudzbine status;
    private LocalDateTime vremePromene;
    private Long promenioKorisnikId;
}
