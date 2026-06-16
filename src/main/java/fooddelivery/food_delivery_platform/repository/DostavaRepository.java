package fooddelivery.food_delivery_platform.repository;



import fooddelivery.food_delivery_platform.model.Dostava;
import fooddelivery.food_delivery_platform.model.StatusDostave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DostavaRepository extends JpaRepository<Dostava, Long> {

    List<Dostava> findByStatus(StatusDostave status);

    List<Dostava> findByDostavljacId(Long dostavljacId);

    Optional<Dostava> findByPorudzbinaId(Long porudzbinaId);
}
