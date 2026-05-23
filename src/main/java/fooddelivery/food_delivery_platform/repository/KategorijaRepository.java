package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Kategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KategorijaRepository extends JpaRepository<Kategorija, Long> {
}