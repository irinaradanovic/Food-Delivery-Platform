package fooddelivery.food_delivery_platform.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class KomboRequestDTO {
    private Long restoranId;
    private BigDecimal maxUkupnaCena;
    private int brojStavki;
    private List<Long> kategorijaIds;   // ID-jevi kategorija umesto string tipova
    private int maxRezultata;

    private Double minUkupneKalorije;
    private Double maxUkupneKalorije;

    private List<Long> iskljuciAlergeneIds;
}