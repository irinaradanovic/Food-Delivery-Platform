package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "racun_stavke")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RacunStavka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "racun_stavka_id")
    private Long racunStavkaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "racun_id", nullable = false)
    @JsonIgnoreProperties({"stavke"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Racun racun;

    @Column(name = "stavka_menija_id")
    private Long stavkaMenijaId;

    @Column(name = "proizvod_id")
    private Long proizvodId;

    private String naziv;

    private Integer kolicina;

    @Column(name = "jedinicna_cena")
    private BigDecimal jedinicnaCena;

    @Column(name = "ukupna_cena")
    private BigDecimal ukupnaCena;

    private String napomena;
}
