package fooddelivery.food_delivery_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KategorijaPreporukeStatDTO {
    private String kategorija;
    private int    prikazano;
    private int    uspesnih;
    private double stopa;
}