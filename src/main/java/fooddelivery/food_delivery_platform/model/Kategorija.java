package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kategorije")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kategorija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kategorijaId;

    private String naziv;

    @Column(name = "kreirao_korisnik_id", nullable = true)
    private Long kreiraoKorisnikId;

    // Tip obroka kome ova kategorija pripada — koristi se za preporuke na nivou korpe
    // umesto krhkog string matchinga po nazivu.
    @Enumerated(EnumType.STRING)
    @Column(name = "tip_obroka", nullable = true)
    private TipObroka tipObroka;
}