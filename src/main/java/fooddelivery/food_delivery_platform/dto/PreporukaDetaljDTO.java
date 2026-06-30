package fooddelivery.food_delivery_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreporukaDetaljDTO {
    private Long   proizvodId;
    private String nazivProizvoda;
    private String kategorija;
    private String tipPreporuke;
    private String prikazanoU;
    private String realizovanoU;
    private boolean uspesna;
}