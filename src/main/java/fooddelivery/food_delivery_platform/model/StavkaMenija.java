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
    @JoinColumn(name = "meniId")
    private Meni meni;

    @ManyToOne
    @JoinColumn(name = "proizvodId")
    private Proizvod proizvod;

    private Integer vremePripremeMin;
    private Integer vremePripremeMax;
    private BigDecimal cena;
    private boolean dostupno;
}