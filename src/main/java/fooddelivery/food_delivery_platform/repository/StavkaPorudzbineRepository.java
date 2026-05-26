package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.StavkaPorudzbine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StavkaPorudzbineRepository extends JpaRepository<StavkaPorudzbine, Long> {
    List<StavkaPorudzbine> findByPorudzbinaPorudzbinaId(Long porudzbinaId);
}