package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prikazane_preporuke")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrikazanaPreporuka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kupac kome je preporuka prikazana (null za globalne: trend, sezonske, vremenske)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kupac_id")
    private Korisnik kupac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proizvod_id", nullable = false)
    private Proizvod proizvod;

    // Tip preporuke koja je prikazana
    @Enumerated(EnumType.STRING)
    @Column(name = "tip_preporuke", nullable = false)
    private TipPreporuke tipPreporuke;

    @Column(name = "prikazano_u", nullable = false)
    private LocalDateTime prikazanoU;

    // Null dok kupac ne naruči; popunjava se u PorudzbinaService.create()
    @Column(name = "realizovano_u")
    private LocalDateTime realizovanoU;

    // True ako je kupac naručio ovaj proizvod nakon što mu je prikazan
    @Builder.Default
    @Column(name = "uspesna")
    private Boolean uspesna = false;

    public enum TipPreporuke {
        PERSONALIZOVANA,
        SEZONSKA,
        VREMENSKA,
        TREND,
        KORPA
    }
}