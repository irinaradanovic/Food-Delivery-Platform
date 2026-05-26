package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.OcenaDostavljaca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcenaDostavljacaRepository extends JpaRepository<OcenaDostavljaca, Long> {

    List<OcenaDostavljaca> findByDostavljacId(Long dostavljacId);
}