package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.NotifikacijaDostavljaca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotifikacijaDostavljacaRepository extends JpaRepository<NotifikacijaDostavljaca, Long> {

    List<NotifikacijaDostavljaca> findByDostavljacId(Long dostavljacId);
}