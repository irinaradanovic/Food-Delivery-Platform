package fooddelivery.food_delivery_platform.model;


import jakarta.persistence.*;

@Entity
@Table(name = "ocene_dostavljaca")
public class OcenaDostavljaca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dostavljac_id")
    private Dostavljac dostavljac;

    private Integer ocena;

    private String komentar;

    @Enumerated(EnumType.STRING)
    private TipOcene tipOcene;

    public OcenaDostavljaca() {
    }

    public Long getId() {
        return id;
    }

    public Dostavljac getDostavljac() {
        return dostavljac;
    }

    public void setDostavljac(Dostavljac dostavljac) {
        this.dostavljac = dostavljac;
    }

    public Integer getOcena() {
        return ocena;
    }

    public void setOcena(Integer ocena) {
        this.ocena = ocena;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public TipOcene getTipOcene() {
        return tipOcene;
    }

    public void setTipOcene(TipOcene tipOcene) {
        this.tipOcene = tipOcene;
    }
}