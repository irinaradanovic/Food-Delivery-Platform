package fooddelivery.food_delivery_platform.dto;

import fooddelivery.food_delivery_platform.model.StatusDostavljaca;

public class DostavljacDTO {

    private Long id;
    private String ime;
    private String prezime;
    private String telefon;
    private Double trenutnaLat;
    private Double trenutnaLng;
    private StatusDostavljaca status;
    private Double prosecnaOcena;
    private Integer brojDostava;

    public DostavljacDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Double getTrenutnaLat() {
        return trenutnaLat;
    }

    public void setTrenutnaLat(Double trenutnaLat) {
        this.trenutnaLat = trenutnaLat;
    }

    public Double getTrenutnaLng() {
        return trenutnaLng;
    }

    public void setTrenutnaLng(Double trenutnaLng) {
        this.trenutnaLng = trenutnaLng;
    }

    public StatusDostavljaca getStatus() {
        return status;
    }

    public void setStatus(StatusDostavljaca status) {
        this.status = status;
    }

    public Double getProsecnaOcena() {
        return prosecnaOcena;
    }

    public void setProsecnaOcena(Double prosecnaOcena) {
        this.prosecnaOcena = prosecnaOcena;
    }

    public Integer getBrojDostava() {
        return brojDostava;
    }

    public void setBrojDostava(Integer brojDostava) {
        this.brojDostava = brojDostava;
    }
}