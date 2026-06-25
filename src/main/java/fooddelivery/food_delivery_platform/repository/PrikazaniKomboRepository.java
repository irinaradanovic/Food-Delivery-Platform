package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.PrikazaniKombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.List;

@Repository
public interface PrikazaniKomboRepository extends JpaRepository<PrikazaniKombo, Long> {

    // Proveri da li postoji vec snimljen kombo za kupca sa istim stavkama u zadatom periodu
    boolean existsByKupac_KorisnikIdAndStavkeMenijaIdsAndPrikazanoUAfter(
            Long kupacId, List<Long> stavkeMenijaIds, LocalDateTime od);

    // Nerealizovani komboi kupca — kandidati za označavanje pri narudžbini
    List<PrikazaniKombo> findByKupac_KorisnikIdAndUspesnaFalseOrderByPrikazanoUDesc(Long kupacId);

    // Analitika — komboi kupca u periodu
    List<PrikazaniKombo> findByKupac_KorisnikIdAndPrikazanoUAfterOrderByPrikazanoUDesc(
            Long kupacId, LocalDateTime od);

    // Analitika — svi komboi u periodu
    List<PrikazaniKombo> findByPrikazanoUAfterOrderByPrikazanoUDesc(LocalDateTime od);
}