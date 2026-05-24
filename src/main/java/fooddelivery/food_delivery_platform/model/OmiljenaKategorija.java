package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "omiljene_kategorije",
        uniqueConstraints = @UniqueConstraint(columnNames = {"korisnik_id", "kategorija_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OmiljenaKategorija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long omiljenaKategorijaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "korisnik_id", nullable = false)
    @ToString.Exclude
    private Kupac kupac;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kategorija_id", nullable = false)
    private Kategorija kategorija;

    @Column(name = "datum_dodavanja")
    @Builder.Default
    private LocalDateTime datumDodavanja = LocalDateTime.now();
}