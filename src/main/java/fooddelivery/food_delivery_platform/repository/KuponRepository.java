package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Kupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KuponRepository extends JpaRepository<Kupon, Long> {
    Optional<Kupon> findByKod(String kod);
}