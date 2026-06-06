package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Proizvod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProizvodRepository extends JpaRepository<Proizvod, Long> {

    List<Proizvod> findByNazivContainingIgnoreCase(String naziv);
    List<Proizvod> findByKategorija_KategorijaId(Long kategorijaId);

    // Kupac - svi proizvodi iz aktivnih menija datog restorana
    @Query("SELECT DISTINCT sm.proizvod FROM StavkaMenija sm " +
            "WHERE sm.meni.restoran.restoranId = :restoranId " +
            "AND sm.meni.aktivan = true " +
            "AND sm.obrisan = false")
    List<Proizvod> findProizvodiIzAktivnihMenija(@Param("restoranId") Long restoranId);

    // Kupac - pretraga po nazivu unutar aktivnih menija restorana
    @Query("SELECT DISTINCT sm.proizvod FROM StavkaMenija sm " +
            "WHERE sm.meni.restoran.restoranId = :restoranId " +
            "AND sm.meni.aktivan = true " +
            "AND sm.obrisan = false " +
            "AND LOWER(sm.proizvod.naziv) LIKE LOWER(CONCAT('%', :naziv, '%'))")
    List<Proizvod> searchProizvodiIzAktivnihMenija(@Param("restoranId") Long restoranId,
                                                   @Param("naziv") String naziv);

    // Kupac - filtriranje po kategoriji unutar aktivnih menija restorana
    @Query("SELECT DISTINCT sm.proizvod FROM StavkaMenija sm " +
            "WHERE sm.meni.restoran.restoranId = :restoranId " +
            "AND sm.meni.aktivan = true " +
            "AND sm.obrisan = false " +
            "AND sm.proizvod.kategorija.kategorijaId = :kategorijaId")
    List<Proizvod> findProizvodiIzAktivnihMenijaByKategorija(@Param("restoranId") Long restoranId,
                                                             @Param("kategorijaId") Long kategorijaId);

    // Sezonske preporuke - proizvodi iz aktivnih SezonskiMeni čija sezona uključuje dati datum
    @Query("SELECT DISTINCT sm.proizvod FROM StavkaMenija sm " +
            "WHERE TYPE(sm.meni) = SezonskiMeni " +
            "AND sm.meni.aktivan = true " +
            "AND sm.obrisan = false " +
            "AND sm.dostupno = true " +
            "AND TREAT(sm.meni AS SezonskiMeni).pocetakSezone <= :danas " +
            "AND TREAT(sm.meni AS SezonskiMeni).krajSezone >= :danas")
    List<Proizvod> findSezonskiProizvodi(@Param("danas") java.time.LocalDate danas);
}