package fooddelivery.food_delivery_platform.repository;


import fooddelivery.food_delivery_platform.model.LokacijaDostavljaca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LokacijaDostavljacaRepository extends JpaRepository<LokacijaDostavljaca, Long> {
}