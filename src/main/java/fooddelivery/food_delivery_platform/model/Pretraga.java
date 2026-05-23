package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pretrage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pretraga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pretragaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "korisnikId", nullable = false)
    @ToString.Exclude
    private Kupac kupac;

    private String tekstUpita;
    private LocalDateTime vremePretrage;
    private String tipPretrage;
}