package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.repository.ProizvodRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProizvodService {

    private final ProizvodRepository proizvodRepository;

    @Autowired
    private StavkaMenijaRepository stavkaMenijaRepository;

    public List<Proizvod> getAll() { return proizvodRepository.findAll(); }

    public Proizvod getById(Long id) {
        return proizvodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proizvod nije pronadjen: " + id));
    }

    public List<Proizvod> getByKategorija(Long id) {
        return proizvodRepository.findByKategorija_KategorijaId(id);
    }

    public List<Proizvod> search(String naziv) {
        return proizvodRepository.findByNazivContainingIgnoreCase(naziv);
    }

    public Proizvod create(Proizvod proizvod) { return proizvodRepository.save(proizvod); }

    public Proizvod update(Long id, Proizvod updated) {
        Proizvod existing = getById(id);
        existing.setNaziv(updated.getNaziv());
        existing.setOpis(updated.getOpis());
        existing.setKalorije(updated.getKalorije());
        existing.setFotografija(updated.getFotografija());
        existing.setKategorija(updated.getKategorija());
        return proizvodRepository.save(existing);
    }

    public void delete(Long id) { proizvodRepository.deleteById(id); }

    // Kupac - svi proizvodi iz aktivnih menija restorana (uz vremenski filter)
    public List<Proizvod> getProizvodiZaKupca(Long restoranId) {
        //return proizvodRepository.findProizvodiIzAktivnihMenija(restoranId, LocalTime.now());

        // pronalazimo stavke menija koje su aktivne za restoran
        List<StavkaMenija> aktivneStavke = stavkaMenijaRepository.findAktivneStavkeZaRestoran(restoranId);

        // uzimamo cenu svakog proizvoda iz stavki menija
        return aktivneStavke.stream().map(stavka -> {
            Proizvod p = stavka.getProizvod();
            p.setCena(stavka.getCena());
            return p;
        }).toList();
    }

    // Kupac - pretraga po nazivu unutar aktivnih menija restorana (uz vremenski filter)
    public List<Proizvod> searchProizvodiZaKupca(Long restoranId, String naziv) {
        return proizvodRepository.searchProizvodiIzAktivnihMenija(restoranId, naziv, LocalTime.now());
    }

    // Kupac - filtriranje po kategoriji unutar aktivnih menija restorana (uz vremenski filter)
    public List<Proizvod> getProizvodiZaKupcaByKategorija(Long restoranId, Long kategorijaId) {
        return proizvodRepository.findProizvodiIzAktivnihMenijaByKategorija(restoranId, kategorijaId, LocalTime.now());
    }
}