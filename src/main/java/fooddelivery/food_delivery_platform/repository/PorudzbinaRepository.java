package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PorudzbinaRepository extends JpaRepository<Porudzbina, Long> {
    List<Porudzbina> findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(Long kupacId);
    List<Porudzbina> findByStatusOrderByDatumKreiranjaDesc(StatusPorudzbine status);
}