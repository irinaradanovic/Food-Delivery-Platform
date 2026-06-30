package fooddelivery.food_delivery_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreporukaAnalitikaDTO {

    private int    ukupnoPrikazano;
    private int    ukupnoUspesnih;
    private double ukupnaStopa;

    private TipPreporukeStatDTO personalizovane;
    private TipPreporukeStatDTO trend;
    private TipPreporukeStatDTO sezonske;
    private TipPreporukeStatDTO vremenske;
    private TipPreporukeStatDTO korpa;

    private List<KategorijaPreporukeStatDTO> poKategoriji;
    private List<PreporukaDetaljDTO>         detalji;
}