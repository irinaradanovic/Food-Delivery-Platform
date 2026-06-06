package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Kategorija;
import fooddelivery.food_delivery_platform.repository.KategorijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KategorijaService {

    @Autowired
    private KategorijaRepository kategorijaRepository;

    public List<Kategorija> getCategoriesForManager(Long trenutniKorisnikId) {
        return kategorijaRepository.findByKreiraoKorisnikIdIsNullOrKreiraoKorisnikId(trenutniKorisnikId);
    }

    public List<Kategorija> getKategorijeZaKupca(Long restoranId) {
        return kategorijaRepository.findKategorijeIzAktivnihMenija(restoranId);
    }
}