package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "klikovi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Klik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long klikId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "korisnikId", nullable = false)
    @ToString.Exclude
    private Kupac kupac;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proizvodId", nullable = false)
    private Proizvod proizvod;

    private LocalDateTime vremeKlika;

    private String tipAkcije; // "PREGLED", "DETALJI", itd.
}