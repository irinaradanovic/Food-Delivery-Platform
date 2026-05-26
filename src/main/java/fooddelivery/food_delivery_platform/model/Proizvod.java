package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

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
    private BigDecimal cena;
    private String fotografija;
    private BigDecimal kolicina;
    private String mernaJedinica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kategorijaId")
    private Kategorija kategorija;

    // ... tvoja polja (naziv, opis, cena...)

    @ManyToMany
    @JoinTable(
            name = "proizvod_alergeni",
            joinColumns = @JoinColumn(name = "proizvodId"),
            inverseJoinColumns = @JoinColumn(name = "alergenId")
    )
    private List<Alergen> alergeni;

    @ManyToMany
    @JoinTable(
            name = "proizvod_sastojci",
            joinColumns = @JoinColumn(name = "proizvodId"),
            inverseJoinColumns = @JoinColumn(name = "sastojakId")
    )
    private List<Sastojak> sastojci;
}