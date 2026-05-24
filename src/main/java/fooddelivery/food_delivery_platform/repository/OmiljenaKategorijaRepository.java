package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.OmiljenaKategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OmiljenaKategorijaRepository extends JpaRepository<OmiljenaKategorija, Long> {
    List<OmiljenaKategorija> findByKupac_KorisnikId(Long kupacId);
    Optional<OmiljenaKategorija> findByKupac_KorisnikIdAndKategorija_KategorijaId(Long kupacId, Long kategorijaId);
    boolean existsByKupac_KorisnikIdAndKategorija_KategorijaId(Long kupacId, Long kategorijaId);
    void deleteByKupac_KorisnikIdAndKategorija_KategorijaId(Long kupacId, Long kategorijaId);
}