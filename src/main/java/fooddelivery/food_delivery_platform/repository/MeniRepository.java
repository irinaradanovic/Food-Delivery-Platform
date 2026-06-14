package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Meni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeniRepository extends JpaRepository<Meni, Long> {
    List<Meni> findByRestoranRestoranId(Long restoranId);

    List<Meni> findByRestoranRestoranIdAndAktivanTrue(Long restoranId);

    @Query(value = "SELECT DISTINCT ON (grupni_meni_id) * FROM meniji " +
            "WHERE restoran_id = :restoranId " +
            "ORDER BY grupni_meni_id, aktivan DESC, meni_id DESC",
            nativeQuery = true)
    List<Meni> findJedinstveniMenijiPoGrupama(@Param("restoranId") Long restoranId);

    List<Meni> findByGrupniMeniIdOrderByVerzijaDesc(Long grupniId);
}