package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PorudzbinaRepository extends JpaRepository<Porudzbina, Long> {
    List<Porudzbina> findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(Long kupacId);
    List<Porudzbina> findByPorudzbinaIdInOrderByDatumKreiranjaDesc(List<Long> porudzbinaIds);
    List<Porudzbina> findByStatusOrderByDatumKreiranjaDesc(StatusPorudzbine status);
    List<Porudzbina> findByStatusAndDatumKreiranjaBefore(StatusPorudzbine status, LocalDateTime datumKreiranja);
    List<Porudzbina> findByKupac_KorisnikIdAndDatumKreiranjaAfterOrderByDatumKreiranjaDesc(Long kupacId, LocalDateTime od);


    // POTREBNO ZA ANALITIKU ZA MENADZERA
    @Query("SELECT COUNT(DISTINCT p) FROM Porudzbina p " +
            "JOIN p.stavke s " +
            "WHERE s.stavkaMenija.meni.meniId = :meniId")
    long countUkupnoPorudzbinaZaMeni(@Param("meniId") Long meniId);

    @Query("SELECT COUNT(DISTINCT p) FROM Porudzbina p " +
            "JOIN p.stavke s " +
            "WHERE s.stavkaMenija.meni.meniId = :meniId " +
            "AND p.status= fooddelivery.food_delivery_platform.model.StatusPorudzbine.POTVRDJENA")
    long countPotvrdjenihPorudzbinaZaMeni(@Param("meniId") Long meniId);

    @Query("SELECT COALESCE(SUM(DISTINCT p.ukupnaCena), 0) FROM Porudzbina p " +
            "JOIN p.stavke s " +
            "WHERE s.stavkaMenija.meni.meniId = :meniId " +
            "AND p.status = fooddelivery.food_delivery_platform.model.StatusPorudzbine.POTVRDJENA")
    BigDecimal sumZaradaOdStavkiMenija(@Param("meniId") Long meniId);
}
