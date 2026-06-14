package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "meniji")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipMenija", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipMenija", // ovo mapira diskriminator direktno u JSON polje tipMenija
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SezonskiMeni.class, name = "SEZONSKI"),
        @JsonSubTypes.Type(value = VremenskiMeni.class, name = "VREMENSKI"),
        @JsonSubTypes.Type(value = StandardniMeni.class, name = "STANDARDNI")
})
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

    @Column(name = "grupni_meni_id")
    private Long grupniMeniId; // ID koji povezuje sve verzije jednog istog menija

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "restoran_id")
    private Restoran restoran;

}