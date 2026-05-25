package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("STANDARDNI")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StandardniMeni extends Meni {

}