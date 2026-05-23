package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.Proizvod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProizvodRepository extends JpaRepository<Proizvod, Long> {
    List<Proizvod> findByNazivContainingIgnoreCase(String naziv);
    List<Proizvod> findByKategorija_KategorijaId(Long kategorijaId);
}