package fooddelivery.food_delivery_platform.dto.menadzerAnalitika;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class PokretaciIzmenaDTO {
    private Long grupniMeniId;
    private long ukupanBrojVerzija;
    private Map<String, Long> brojPoRazlogu;
    private Map<String, Double> procenatPoRazlogu;
    private String menadzerskiZakljucak;
}
