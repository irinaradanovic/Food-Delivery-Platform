package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prikazani_komboi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrikazaniKombo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kupac_id", nullable = false)
    private Korisnik kupac;

    // Stavke menija koje čine ovaj kombo — čuvamo kao @ElementCollection
    // da ne pravimo poseban join entitet
    @ElementCollection
    @CollectionTable(
            name = "prikazani_kombo_stavke",
            joinColumns = @JoinColumn(name = "kombo_id")
    )
    @Column(name = "stavka_menija_id")
    private List<Long> stavkeMenijaIds;

    @Column(name = "prikazano_u", nullable = false)
    private LocalDateTime prikazanoU;

    @Column(name = "realizovano_u")
    private LocalDateTime realizovanoU;

    // true ako je kupac naručio bar jednu stavku iz komba
    @Builder.Default
    @Column(name = "uspesna")
    private Boolean uspesna = false;

    // Koliko stavki iz komba je naručeno (0, 1, 2, 3...)
    @Builder.Default
    @Column(name = "broj_narucenih_stavki")
    private Integer brojNarucenihStavki = 0;
}