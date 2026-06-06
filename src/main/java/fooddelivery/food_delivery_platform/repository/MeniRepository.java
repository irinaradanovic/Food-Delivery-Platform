package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Meni;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeniRepository extends JpaRepository<Meni, Long> {
    List<Meni> findByRestoranRestoranId(Long restoranId);

    List<Meni> findByRestoranRestoranIdAndAktivanTrue(Long restoranId);
}