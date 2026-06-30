package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.StatusPorudzbineIstorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusPorudzbineIstorijaRepository extends JpaRepository<StatusPorudzbineIstorija, Long> {
    List<StatusPorudzbineIstorija> findByPorudzbinaPorudzbinaIdOrderByVremePromeneAsc(Long porudzbinaId);
    Optional<StatusPorudzbineIstorija> findTopByPorudzbinaPorudzbinaIdOrderByVremePromeneDesc(Long porudzbinaId);
    List<StatusPorudzbineIstorija> findByPorudzbinaPorudzbinaIdInOrderByVremePromeneAsc(List<Long> porudzbinaIds);
}
