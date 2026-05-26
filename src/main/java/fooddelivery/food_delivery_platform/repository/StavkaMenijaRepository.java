package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.StavkaMenija;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StavkaMenijaRepository extends JpaRepository<StavkaMenija, Long> {
    List<StavkaMenija> findByMeniMeniIdAndObrisanFalse(Long meniId);
}
