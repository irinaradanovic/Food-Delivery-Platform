package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StavkaMenijaRepository extends JpaRepository<StavkaMenija, Long> {
    List<StavkaMenija> findByMeniMeniIdAndObrisanFalse(Long meniId);
    Optional<StavkaMenija> findByMeniAndProizvod(Meni meni, Proizvod proizvod);

    @Query("SELECT s FROM StavkaMenija s " +
            "JOIN s.meni m " +
            "WHERE m.restoran.restoranId = :restoranId " +
            "AND m.aktivan = true " +
            "AND s.obrisan = false")
    List<StavkaMenija> findAktivneStavkeZaRestoran(@Param("restoranId") Long restoranId);

    @Query("SELECT s FROM StavkaMenija s " +
            "JOIN s.proizvod " +
            "WHERE s.proizvod.proizvodId IN :ids " +
            "AND s.meni.aktivan = true " +
            "AND s.obrisan = false")
    List<StavkaMenija> findAktivneCeneZaProizvode(@Param("ids") Set<Long> ids);

    @Query(value = "SELECT COALESCE(AVG(s.vreme_pripreme_min), 0), COALESCE(AVG(s.vreme_pripreme_max), 0) " +
            "FROM stavke_menija s " +
            "JOIN proizvodi p ON s.proizvod_id = p.proizvod_id " +
            "WHERE p.kategorija_id = :kategorijaId", nativeQuery = true)
    Object[] findAverageTimeByKategorija(@Param("kategorijaId") Long kategorijaId);
}
