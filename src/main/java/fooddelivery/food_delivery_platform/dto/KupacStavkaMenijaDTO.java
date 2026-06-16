package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.Alergen;
import fooddelivery.food_delivery_platform.model.Kategorija;
import fooddelivery.food_delivery_platform.model.Sastojak;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class KupacStavkaMenijaDTO {
    private Long stavkaMenijaId;
    private Long proizvodId;
    private String naziv;
    private String opis;
    private BigDecimal kalorije;
    private BigDecimal cena;
    private String fotografija;
    private BigDecimal kolicina;
    private String mernaJedinica;
    private Kategorija kategorija;
    private List<Alergen> alergeni;
    private List<Sastojak> sastojci;
    private Integer vremePripremeMin;
    private Integer vremePripremeMax;
    private boolean dostupno;
}
