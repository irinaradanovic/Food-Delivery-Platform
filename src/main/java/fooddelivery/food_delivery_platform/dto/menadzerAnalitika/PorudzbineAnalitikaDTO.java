package fooddelivery.food_delivery_platform.dto.menadzerAnalitika;

import fooddelivery.food_delivery_platform.model.NacinPlacanja;
import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PorudzbineAnalitikaDTO {
    private LocalDate datumOd;
    private LocalDate datumDo;
    private Long restoranId;
    private String restoranNaziv;
    private List<RestoranFilterDTO> restorani;
    private SazetakPorudzbinaDTO sazetak;
    private KuponiPorudzbinaDTO kuponi;
    private List<DnevniTrendDTO> dnevniTrend;
    private List<StatusPorudzbinaDTO> statusi;
    private List<TopStavkaDTO> topStavke;
    private List<SatniTrendDTO> satniTrend;
    private List<PlacanjeTrendDTO> placanja;
    private List<NedavnaPorudzbinaDTO> nedavnePorudzbine;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestoranFilterDTO {
        private Long restoranId;
        private String naziv;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SazetakPorudzbinaDTO {
        private long ukupanBrojPorudzbina;
        private long aktivnePorudzbine;
        private long isporucenePorudzbine;
        private long otkazanePorudzbine;
        private BigDecimal prometArtikala;
        private BigDecimal prosecnaVrednostPorudzbine;
        private double stopaOtkazivanja;
        private Double prosecnoVremeDoPotvrdeMin;
        private Double prosecnoVremeDoIsporukeMin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KuponiPorudzbinaDTO {
        private long brojPorudzbinaSaKuponom;
        private BigDecimal ukupanPopust;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DnevniTrendDTO {
        private LocalDate datum;
        private long brojPorudzbina;
        private BigDecimal promet;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusPorudzbinaDTO {
        private StatusPorudzbine status;
        private long brojPorudzbina;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopStavkaDTO {
        private Long stavkaMenijaId;
        private Long proizvodId;
        private String naziv;
        private String kategorija;
        private long kolicina;
        private BigDecimal promet;
        private BigDecimal prosecnaCena;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SatniTrendDTO {
        private int sat;
        private long brojPorudzbina;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlacanjeTrendDTO {
        private NacinPlacanja nacinPlacanja;
        private long brojPorudzbina;
        private BigDecimal promet;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NedavnaPorudzbinaDTO {
        private Long porudzbinaId;
        private LocalDateTime datumKreiranja;
        private StatusPorudzbine status;
        private NacinPlacanja nacinPlacanja;
        private BigDecimal prometMenadzera;
        private Long brojStavki;
    }
}
