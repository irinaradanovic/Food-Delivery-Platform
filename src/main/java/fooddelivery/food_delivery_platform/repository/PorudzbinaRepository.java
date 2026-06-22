package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PorudzbinaRepository extends JpaRepository<Porudzbina, Long> {
    List<Porudzbina> findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(Long kupacId);
    List<Porudzbina> findByPorudzbinaIdInOrderByDatumKreiranjaDesc(List<Long> porudzbinaIds);
    List<Porudzbina> findByStatusOrderByDatumKreiranjaDesc(StatusPorudzbine status);
    List<Porudzbina> findByStatusAndDatumKreiranjaBefore(StatusPorudzbine status, LocalDateTime datumKreiranja);
    List<Porudzbina> findByKupac_KorisnikIdAndDatumKreiranjaAfterOrderByDatumKreiranjaDesc(Long kupacId, LocalDateTime od);
}
