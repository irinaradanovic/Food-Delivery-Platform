package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stavke_menija")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StavkaMenija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stavkaId;

    @ManyToOne
    @JoinColumn(name = "meni_id")
    private Meni meni;

    @ManyToOne
    @JoinColumn(name = "proizvod_id")
    private Proizvod proizvod;

    @Column(name = "vreme_pripreme_min")
    private Integer vremePripremeMin;

    @Column(name = "vreme_pripreme_max")
    private Integer vremePripremeMax;
    private BigDecimal cena;
    private boolean dostupno;

    @Builder.Default
    private boolean obrisan = false;
}