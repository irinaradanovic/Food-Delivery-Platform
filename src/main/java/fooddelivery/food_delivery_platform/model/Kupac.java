package fooddelivery.food_delivery_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kupci")
@PrimaryKeyJoinColumn(name = "korisnikId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Kupac extends Korisnik {

    private String adresa;
    private String brojKartice;


    @OneToMany(mappedBy = "kupac", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Klik> klikovi = new ArrayList<>();

    @OneToMany(mappedBy = "kupac", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Pretraga> pretrage = new ArrayList<>();

    @OneToMany(mappedBy = "kupac", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<OmiljeniProizvod> omiljeniProizvodi = new ArrayList<>();

    @OneToMany(mappedBy = "vlasnik", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Kupon> kuponi = new ArrayList<>();
}