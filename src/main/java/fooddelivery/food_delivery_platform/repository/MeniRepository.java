package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.SezonskiMeni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MeniRepository extends JpaRepository<Meni, Long> {
    List<Meni> findByRestoranRestoranId(Long restoranId);

    @Query("SELECT m FROM Meni m WHERE m.restoran.restoranId = :restoranId AND m.aktivan = true ORDER BY m.meniId DESC")
    List<Meni> findByRestoranRestoranIdAndAktivanTrue(Long restoranId);


    @Query("SELECT m FROM Meni m WHERE m.restoran.restoranId = :restoranId AND m.aktivan = true " +
            "AND (TYPE(m) <> VremenskiMeni " +
            "OR (TREAT(m AS VremenskiMeni).vremeOd <= :trenutnoVreme " +
            "AND TREAT(m AS VremenskiMeni).vremeDo >= :trenutnoVreme))" +
            "AND (TYPE(m) <> SezonskiMeni " +
            "OR (TREAT(m AS SezonskiMeni).pocetakSezone <= CURRENT_DATE AND TREAT(m AS SezonskiMeni).krajSezone >= CURRENT_DATE))")
    List<Meni> findAktivniZaKupcaSaVremenskimFilterom(
            @Param("restoranId") Long restoranId,
            @Param("trenutnoVreme") LocalTime trenutnoVreme);

    @Query(value = "SELECT DISTINCT ON (grupni_meni_id) * FROM meniji " +
            "WHERE restoran_id = :restoranId " +
            "ORDER BY grupni_meni_id, aktivan DESC, meni_id DESC",
            nativeQuery = true)
    List<Meni> findJedinstveniMenijiPoGrupama(@Param("restoranId") Long restoranId);

    List<Meni> findByGrupniMeniIdOrderByVerzijaDesc(Long grupniId);

    Optional<Meni> findByGrupniMeniIdAndAktivanTrue(Long grupniMeniId);

    @Query("SELECT MAX(CAST(SUBSTRING(m.verzija, 2) AS int)) FROM Meni m WHERE m.grupniMeniId = :grupniId")
    Integer findMaxVersionNumberByGrupniId(@Param("grupniId") Long grupniId);

    @Query("SELECT m FROM Meni m"
    + " WHERE TYPE(m) = SezonskiMeni ")
    List<SezonskiMeni> findAllSeasonalMenus();

    Meni findMeniByMeniId(Long id);


}