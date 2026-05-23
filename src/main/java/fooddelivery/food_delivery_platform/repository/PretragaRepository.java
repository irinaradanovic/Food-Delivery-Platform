package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Pretraga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PretragaRepository extends JpaRepository<Pretraga, Long> {
    List<Pretraga> findByKupac_KorisnikIdOrderByVremePretrageDesc(Long kupacId);
}