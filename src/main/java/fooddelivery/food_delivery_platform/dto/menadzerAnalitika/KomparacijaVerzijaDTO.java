package fooddelivery.food_delivery_platform.dto.menadzerAnalitika;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class KomparacijaVerzijaDTO {
    private MeniStatistikaDTO meniA;
    private MeniStatistikaDTO meniB;
}
