package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Alergen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlergenRepository extends JpaRepository<Alergen, Long> {
}
