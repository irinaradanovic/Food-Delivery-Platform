package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("VREMENSKI")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VremenskiMeni extends Meni {
    private LocalTime vremeOd;
    private LocalTime vremeDo;
}