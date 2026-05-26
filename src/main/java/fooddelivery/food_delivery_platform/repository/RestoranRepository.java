package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Restoran;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestoranRepository extends JpaRepository<Restoran, Long> {
    List<Restoran> findByMenadzerKorisnikId(Long menadzerId);
}
