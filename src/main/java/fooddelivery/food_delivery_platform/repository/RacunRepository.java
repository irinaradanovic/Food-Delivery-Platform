package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Racun;
import fooddelivery.food_delivery_platform.model.TipRacuna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RacunRepository extends JpaRepository<Racun, Long> {
    Optional<Racun> findByPorudzbinaPorudzbinaIdAndTip(Long porudzbinaId, TipRacuna tip);
    boolean existsByPorudzbinaPorudzbinaIdAndTip(Long porudzbinaId, TipRacuna tip);
}
