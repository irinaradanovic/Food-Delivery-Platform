package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kuponi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kupon_id")
    private Long kuponId;

    @Column(name = "kod", unique = true, nullable = false)
    private String kod;

    @Column(name = "popust_iznos")
    private BigDecimal popustIznos; // fiksni iznos

    @Column(name = "popust_procenat")
    private BigDecimal popustProcenat; // može biti NULL

    @Column(name = "vazi_od")
    private LocalDateTime vaziOd;

    @Column(name = "vazi_do")
    private LocalDateTime vaziDo;

    private boolean aktivan = true;

    @Column(name = "max_upotreba")
    private Integer maxUpotreba;

    @Column(name = "upotrebljeno_puta")
    private Integer upotrebljenoPuta = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vlasnik_id")
    @JsonIgnoreProperties({"klikovi", "pretrage", "omiljeniProizvodi", "kuponi"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Kupac vlasnik;
}