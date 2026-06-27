package fooddelivery.food_delivery_platform.dto.menadzerAnalitika;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MeniStatistikaDTO {
    private String verzija;
    private String razlogVerzionisanja;
    private LocalDate datumOd;
    private LocalDate datumDo;
    private long brojStavki;
    private long brojKategorija;
    private double prosecnaCenaStavki;

    private long ukupanBrojPorudzbina;
    private long brojPotvrdjenihPorudzbina;
    private BigDecimal ukupnaZarada;
}
