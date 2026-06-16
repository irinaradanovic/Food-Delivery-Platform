package fooddelivery.food_delivery_platform.model;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "dostavljaci")
public class Dostavljac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "korisnik_id")
    private Long id;

    private String ime;

    private String prezime;

    private String telefon;

    private Double trenutnaLat;

    private Double trenutnaLng;

    @Enumerated(EnumType.STRING)
    private StatusDostavljaca status;

    private Double prosecnaOcena;

    private Integer brojDostava;

    @OneToMany(mappedBy = "dostavljac")
    private List<Dostava> dostave;

    public Dostavljac() {
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
