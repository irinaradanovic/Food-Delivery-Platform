package fooddelivery.food_delivery_platform.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MeniUpdateDTO {
    private String naziv;
    private String opis;

    // za sezonski meni
    private LocalDate pocetakSezone;
    private LocalDate krajSezone;

    // za vremenski meni
    private LocalTime vremeOd;
    private LocalTime vremeDo;
}