package fooddelivery.food_delivery_platform.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dostave")
public class Dostava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long porudzbinaId;

    @ManyToOne
    @JoinColumn(name = "dostavljac_id")
    private Dostavljac dostavljac;

    private String adresaPreuzimanja;

    private String adresaIsporuke;

    private LocalDateTime vremeKreiranja;

    private LocalDateTime vremePreuzimanja;

    private LocalDateTime vremeIsporuke;

    private Integer procenjenoVreme;

    @Enumerated(EnumType.STRING)
    private StatusDostave status;

    public Dostava() {
    }

    public Long getId() {
        return id;
    }

    public Long getPorudzbinaId() {
        return porudzbinaId;
    }

    public void setPorudzbinaId(Long porudzbinaId) {
        this.porudzbinaId = porudzbinaId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dostavljac getDostavljac() {
        return dostavljac;
    }

    public void setDostavljac(Dostavljac dostavljac) {
        this.dostavljac = dostavljac;
    }

    public String getAdresaPreuzimanja() {
        return adresaPreuzimanja;
    }

    public void setAdresaPreuzimanja(String adresaPreuzimanja) {
        this.adresaPreuzimanja = adresaPreuzimanja;
    }

    public String getAdresaIsporuke() {
        return adresaIsporuke;
    }

    public void setAdresaIsporuke(String adresaIsporuke) {
        this.adresaIsporuke = adresaIsporuke;
    }

    public LocalDateTime getVremeKreiranja() {
        return vremeKreiranja;
    }

    public void setVremeKreiranja(LocalDateTime vremeKreiranja) {
        this.vremeKreiranja = vremeKreiranja;
    }

    public LocalDateTime getVremePreuzimanja() {
        return vremePreuzimanja;
    }

    public void setVremePreuzimanja(LocalDateTime vremePreuzimanja) {
        this.vremePreuzimanja = vremePreuzimanja;
    }

    public LocalDateTime getVremeIsporuke() {
        return vremeIsporuke;
    }

    public void setVremeIsporuke(LocalDateTime vremeIsporuke) {
        this.vremeIsporuke = vremeIsporuke;
    }

    public Integer getProcenjenoVreme() {
        return procenjenoVreme;
    }

    public void setProcenjenoVreme(Integer procenjenoVreme) {
        this.procenjenoVreme = procenjenoVreme;
    }

    public StatusDostave getStatus() {
        return status;
    }

    public void setStatus(StatusDostave status) {
        this.status = status;
    }
}