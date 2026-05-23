package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "korisnici")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long korisnikId;

    private String ime;
    private String prezime;
    private String telefon;
    private String lozinka;
    private String email;
    private String datumReg;
    private String uloga;
}