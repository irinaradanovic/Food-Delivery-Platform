package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Kupac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KupacRepository extends JpaRepository<Kupac, Long> {
    Optional<Kupac> findByEmail(String email);
    boolean existsByEmail(String email);
}