package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.StatusDostave;

import java.time.LocalDateTime;

public class DostavaDTO {

    private Long id;
    private Long porudzbinaId;
    private Long dostavljacId;
    private String adresaPreuzimanja;
    private String adresaIsporuke;
    private LocalDateTime vremeKreiranja;
    private LocalDateTime vremeIsporuke;
    private Integer procenjenoVreme;
    private StatusDostave status;

    public DostavaDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPorudzbinaId() {
        return porudzbinaId;
    }

    public void setPorudzbinaId(Long porudzbinaId) {
        this.porudzbinaId = porudzbinaId;
    }

    public Long getDostavljacId() {
        return dostavljacId;
    }

    public void setDostavljacId(Long dostavljacId) {
        this.dostavljacId = dostavljacId;
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