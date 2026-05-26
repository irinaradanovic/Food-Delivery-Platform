package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "meniji")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipMenija", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Meni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meniId;
    private String naziv;
    private String opis;
    private String verzija = "v1"; // Automatski v1 pri kreiranju
    private boolean aktivan;
    private LocalDate datumOd;
    private LocalDate datumDo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "restoranId")
    private Restoran restoran;

}