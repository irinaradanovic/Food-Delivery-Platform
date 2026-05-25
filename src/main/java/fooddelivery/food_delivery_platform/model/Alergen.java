package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alergeni")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alergen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alergenId;
    private String naziv;
}