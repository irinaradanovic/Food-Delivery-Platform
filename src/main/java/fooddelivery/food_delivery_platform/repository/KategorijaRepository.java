package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Kategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KategorijaRepository extends JpaRepository<Kategorija, Long> {

    List<Kategorija> findByKreiraoKorisnikIdIsNullOrKreiraoKorisnikId(Long kreiraoKorisnikId);
}