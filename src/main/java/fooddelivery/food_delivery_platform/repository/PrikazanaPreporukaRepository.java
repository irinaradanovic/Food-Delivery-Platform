package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.PrikazanaPreporuka;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrikazanaPreporukaRepository extends JpaRepository<PrikazanaPreporuka, Long> {

    // Sve nerealizovane preporuke za kupca — kandidati za označavanje pri narudžbini
    List<PrikazanaPreporuka> findByKupac_KorisnikIdAndUspesnaFalseOrderByPrikazanoUDesc(Long kupacId);

    // Analitika — sve preporuke kupca u periodu
    List<PrikazanaPreporuka> findByKupac_KorisnikIdAndPrikazanoUAfterOrderByPrikazanoUDesc(
            Long kupacId, LocalDateTime od);

    // Analitika — globalne preporuke (trend/sezonske) bez filtera po kupcu
    List<PrikazanaPreporuka> findByTipPreporukeAndPrikazanoUAfterOrderByPrikazanoUDesc(
            PrikazanaPreporuka.TipPreporuke tip, LocalDateTime od);

    // Batch update — označi kao uspešne sve preporuke kupca za date proizvode
    @Modifying
    @Query("""
        UPDATE PrikazanaPreporuka p
        SET p.uspesna = true, p.realizovanoU = :sada
        WHERE p.kupac.korisnikId = :kupacId
          AND p.proizvod.proizvodId IN :proizvodiIds
          AND p.uspesna = false
    """)
    int oznaciKaoUspesne(
            @Param("kupacId") Long kupacId,
            @Param("proizvodiIds") List<Long> proizvodiIds,
            @Param("sada") LocalDateTime sada);
}