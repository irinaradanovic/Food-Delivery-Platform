package fooddelivery.food_delivery_platform.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IzmenaStavkeMenijaDTO {
    private String naziv;
    private String opis;
    private BigDecimal kalorije;
    private BigDecimal kolicina;
    private String mernaJedinica;

    private Long kategorijaId;
    private String novaKategorijaNaziv;

    private List<Long> sastojciIds;
    private List<String> noviSastojciNazivi;

    private List<Long> alergeniIds;

    private BigDecimal cena;
    private Integer vremePripremeMin;
    private Integer vremePripremeMax;
}