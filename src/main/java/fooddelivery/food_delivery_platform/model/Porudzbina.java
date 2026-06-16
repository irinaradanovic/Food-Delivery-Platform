package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "porudzbine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Porudzbina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "porudzbina_id")
    private Long porudzbinaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "korisnik_id")
    @JsonIgnoreProperties({"klikovi", "pretrage", "omiljeniProizvodi"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Kupac kupac;

    @Column(name = "adresa_dostave")
    private String adresaDostave;

    @Column(name = "datum_kreiranja")
    private LocalDateTime datumKreiranja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kupon_id")
    @JsonIgnoreProperties({"vlasnik"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Kupon kupon;

    private String napomena;

    @Enumerated(EnumType.STRING)
    private StatusPorudzbine status;

    @Enumerated(EnumType.STRING)
    @Column(name = "nacin_placanja")
    private NacinPlacanja nacinPlacanja;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_placanja")
    private StatusPlacanja statusPlacanja;

    @Column(name = "cena_artikala")
    private BigDecimal cenaArtikala;

    @Column(name = "cena_dostave")
    private BigDecimal cenaDostave;

    @Column(name = "popust_artikli")
    private BigDecimal popustArtikli;

    @Column(name = "popust_dostava")
    private BigDecimal popustDostava;

    @Column(name = "iznos_karticom")
    private BigDecimal iznosKarticom;

    @Column(name = "iznos_kes")
    private BigDecimal iznosKes;

    @Column(name = "ukupna_cena")
    private BigDecimal ukupnaCena;

    @Transient
    private Long dostavljacId;

    @OneToMany(mappedBy = "porudzbina", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StavkaPorudzbine> stavke = new ArrayList<>();

    @OneToMany(mappedBy = "porudzbina", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StatusPorudzbineIstorija> istorijaStatusa = new ArrayList<>();
}
