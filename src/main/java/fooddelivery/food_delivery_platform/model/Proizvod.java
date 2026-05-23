package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "proizvodi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proizvod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proizvodId;

    private String naziv;
    private String opis;
    private BigDecimal kalorije;
    private String fotografija;
    private BigDecimal kolicina;
    private String mernaJedinica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kategorijaId")
    private Kategorija kategorija;
}