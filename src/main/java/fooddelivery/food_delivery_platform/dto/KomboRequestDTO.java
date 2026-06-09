package fooddelivery.food_delivery_platform.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class KomboRequestDTO {
    private Long restoranId;
    private BigDecimal maxUkupnaCena;
    private int brojStavki;
    private List<String> tipovi;
    private int maxRezultata;

    // Kalorije — opcionalni filter na zbir kalorija celog komba
    private Double minUkupneKalorije;
    private Double maxUkupneKalorije;

    // Alergeni koje treba isključiti — lista ID-jeva
    private List<Long> iskljuciAlergeneIds;
}