package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sastojci")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sastojak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sastojakId;
    private String naziv;
}