package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Meni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface MeniRepository extends JpaRepository<Meni, Long> {
    List<Meni> findByRestoranRestoranId(Long restoranId);

    List<Meni> findByRestoranRestoranIdAndAktivanTrue(Long restoranId);


    @Query("SELECT m FROM Meni m WHERE m.restoran.restoranId = :restoranId AND m.aktivan = true " +
            "AND (m.vremeOd IS NULL OR (m.vremeOd <= :trenutnoVreme AND m.vremeDo >= :trenutnoVreme))")
    List<Meni> findAktivniZaKupcaSaVremenskimFilterom(
            @Param("restoranId") Long restoranId,
            @Param("trenutnoVreme") LocalTime trenutnoVreme);
}