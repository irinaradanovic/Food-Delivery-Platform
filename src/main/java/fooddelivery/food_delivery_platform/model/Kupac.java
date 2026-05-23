package fooddelivery.food_delivery_platform.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kupci")
@PrimaryKeyJoinColumn(name = "korisnikId")
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
    private List<Klik> klikovi = new ArrayList<>();

    @OneToMany(mappedBy = "kupac", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Pretraga> pretrage = new ArrayList<>();

    @OneToMany(mappedBy = "kupac", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OmiljeniProizvod> omiljeniProizvodi = new ArrayList<>();
}