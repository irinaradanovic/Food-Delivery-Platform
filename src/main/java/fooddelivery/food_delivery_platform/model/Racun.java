package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "racuni", uniqueConstraints = {
        @UniqueConstraint(name = "uk_racun_broj", columnNames = "broj_racuna")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Racun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "racun_id")
    private Long racunId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "porudzbina_id", nullable = false)
    @JsonIgnoreProperties({"stavke", "istorijaStatusa"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Porudzbina porudzbina;

    @Column(name = "broj_racuna", unique = true)
    private String brojRacuna;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip", nullable = false)
    private TipRacuna tip;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusRacuna status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originalni_racun_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Racun originalniRacun;

    @Column(name = "datum_izdavanja", nullable = false)
    private LocalDateTime datumIzdavanja;

    @Column(name = "izdao_korisnik_id")
    private Long izdaoKorisnikId;

    @Column(name = "cena_artikala")
    private BigDecimal cenaArtikala;

    @Column(name = "cena_dostave")
    private BigDecimal cenaDostave;

    @Column(name = "popust_artikli")
    private BigDecimal popustArtikli;

    @Column(name = "popust_dostava")
    private BigDecimal popustDostava;

    @Column(name = "ukupna_cena")
    private BigDecimal ukupnaCena;

    @Enumerated(EnumType.STRING)
    @Column(name = "nacin_placanja")
    private NacinPlacanja nacinPlacanja;

    @OneToMany(mappedBy = "racun", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RacunStavka> stavke = new ArrayList<>();
}
