package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Sastojak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SastojakRepository extends JpaRepository<Sastojak, Long> {
    List<Sastojak> findByKreiraoKorisnikIdIsNullOrKreiraoKorisnikId(Long kreiraoKorisnikId);
}
