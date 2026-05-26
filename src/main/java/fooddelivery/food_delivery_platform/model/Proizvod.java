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
    @Column(name = "proizvod_id")
    private Long proizvodId;

    private String naziv;
    private String opis;
    private BigDecimal kalorije;
    private BigDecimal cena;
    private String fotografija;
    private BigDecimal kolicina;

    @Column(name = "merna_jedinica")
    private String mernaJedinica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kategorija_id")
    private Kategorija kategorija;

    @ManyToMany
    @JoinTable(
            name = "proizvod_alergeni",
            joinColumns = @JoinColumn(name = "proizvodId"),
            inverseJoinColumns = @JoinColumn(name = "alergen_id")
    )
    private List<Alergen> alergeni;

    @ManyToMany
    @JoinTable(
            name = "proizvod_sastojci",
            joinColumns = @JoinColumn(name = "proizvodId"),
            inverseJoinColumns = @JoinColumn(name = "sastojak_id")
    )
    private List<Sastojak> sastojci;
}