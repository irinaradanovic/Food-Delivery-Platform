package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Kategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KategorijaRepository extends JpaRepository<Kategorija, Long> {

    List<Kategorija> findByKreiraoKorisnikIdIsNullOrKreiraoKorisnikId(Long kreiraoKorisnikId);

    @Query("SELECT DISTINCT sm.proizvod.kategorija FROM StavkaMenija sm " +
            "WHERE sm.meni.restoran.restoranId = :restoranId " +
            "AND sm.meni.aktivan = true " +
            "AND sm.obrisan = false " +
            "AND sm.proizvod.kategorija IS NOT NULL")
    List<Kategorija> findKategorijeIzAktivnihMenija(@Param("restoranId") Long restoranId);
}