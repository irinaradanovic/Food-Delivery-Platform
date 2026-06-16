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
            "AND s.obrisan = false" +
            " AND s.dostupno = true")
    List<StavkaMenija> findAktivneStavkeZaRestoran(@Param("restoranId") Long restoranId);

    @Query("SELECT s FROM StavkaMenija s " +
            "JOIN s.proizvod " +
            "WHERE s.proizvod.proizvodId IN :ids " +
            "AND s.meni.aktivan = true " +
            "AND s.obrisan = false")
    List<StavkaMenija> findAktivneCeneZaProizvode(@Param("ids") Set<Long> ids);


    @Query("SELECT COALESCE(AVG(s.vremePripremeMin),0) FROM StavkaMenija s " +
            "WHERE s.proizvod.kategorija.kategorijaId = :kategorijaId AND s.obrisan = false")
    Double findAvgMinByKategorija(@Param("kategorijaId") Long kategorijaId);

    @Query("SELECT COALESCE(AVG(s.vremePripremeMax),0) FROM StavkaMenija s " +
            "WHERE s.proizvod.kategorija.kategorijaId = :kategorijaId AND s.obrisan = false")
    Double findAvgMaxByKategorija(@Param("kategorijaId") Long kategorijaId);


}
