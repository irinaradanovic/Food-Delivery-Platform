package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("SEZONSKI")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SezonskiMeni extends Meni {
    private LocalDate pocetakSezone;
    private LocalDate krajSezone;
}