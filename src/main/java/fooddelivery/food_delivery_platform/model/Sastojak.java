package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sastojci")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sastojak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sastojakId;
    private String naziv;

    @Column(name = "kreirao_korisnik_id", nullable = true)
    private Long kreiraoKorisnikId;
}