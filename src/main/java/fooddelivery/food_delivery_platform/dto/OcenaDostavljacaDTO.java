package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.TipOcene;

public class OcenaDostavljacaDTO {

    private Long dostavljacId;
    private Integer ocena;
    private String komentar;
    private TipOcene tipOcene;

    public OcenaDostavljacaDTO() {
    }

    public Long getDostavljacId() {
        return dostavljacId;
    }

    public void setDostavljacId(Long dostavljacId) {
        this.dostavljacId = dostavljacId;
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
