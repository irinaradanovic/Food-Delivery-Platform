package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restorani")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restoran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restoranId;

    private String naziv;
    private String adresa;
    private String kontakt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menadzer_id")
    private Menadzer menadzer;
}