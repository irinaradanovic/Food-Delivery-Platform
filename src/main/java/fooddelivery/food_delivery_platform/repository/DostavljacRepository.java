package fooddelivery.food_delivery_platform.repository;



import fooddelivery.food_delivery_platform.model.Dostavljac;
import fooddelivery.food_delivery_platform.model.StatusDostavljaca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DostavljacRepository extends JpaRepository<Dostavljac, Long> {

    List<Dostavljac> findByStatus(StatusDostavljaca status);
}