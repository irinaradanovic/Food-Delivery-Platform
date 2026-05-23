package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Klik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KlikRepository extends JpaRepository<Klik, Long> {
    List<Klik> findByKupac_KorisnikIdOrderByVremeKlikaDesc(Long kupacId);
    List<Klik> findByProizvod_ProizvodIdOrderByVremeKlikaDesc(Long proizvodId);
}