package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "omiljeni_proizvodi",
        uniqueConstraints = @UniqueConstraint(columnNames = {"korisnikId", "proizvodId"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OmiljeniProizvod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long omiljeniId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "korisnikId", nullable = false)
    @ToString.Exclude
    private Kupac kupac;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proizvodId", nullable = false)
    private Proizvod proizvod;

    @Builder.Default
    private LocalDateTime datumDodavanja = LocalDateTime.now();
}