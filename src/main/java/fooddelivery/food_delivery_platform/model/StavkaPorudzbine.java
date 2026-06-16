package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stavke_porudzbine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StavkaPorudzbine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stavka_id")
    private Long stavkaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "porudzbina_id")
    @JsonIgnoreProperties({"stavke", "istorijaStatusa"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Porudzbina porudzbina;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stavka_menija_id")
    private StavkaMenija stavkaMenija;

    private Integer kolicina;

    private BigDecimal cena;

    private String napomena;
}
