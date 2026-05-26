package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menadzeri")
@PrimaryKeyJoinColumn(name = "korisnikId")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Menadzer extends Korisnik {
}