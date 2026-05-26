package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "statusi_porudzbine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusPorudzbineIstorija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_istorija_id")
    private Long statusIstorijaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "porudzbina_id")
    @JsonIgnoreProperties({"stavke", "istorijaStatusa"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Porudzbina porudzbina;

    @Enumerated(EnumType.STRING)
    private StatusPorudzbine status;

    @Column(name = "vreme_promene")
    private LocalDateTime vremePromene;

    @Column(name = "promenio_korisnik_id")
    private Long promenioKorisnikId;
}