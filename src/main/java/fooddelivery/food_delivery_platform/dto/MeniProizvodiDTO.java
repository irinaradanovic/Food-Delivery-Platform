package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.Proizvod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeniProizvodiDTO {
    private Long meniId;
    private String naziv;
    private String opis;
    // za SezonskiMeni
    private LocalDate pocetakSezone;
    private LocalDate krajSezone;
    // za VremenskiMeni
    private LocalTime vremeOd;
    private LocalTime vremeDo;
    private List<Proizvod> proizvodi;
}
