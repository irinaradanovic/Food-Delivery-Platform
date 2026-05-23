package fooddelivery.food_delivery_platform.repository;

import fooddelivery.food_delivery_platform.model.OmiljeniProizvod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OmiljeniProizvodRepository extends JpaRepository<OmiljeniProizvod, Long> {
    List<OmiljeniProizvod> findByKupac_KorisnikId(Long kupacId);
    Optional<OmiljeniProizvod> findByKupac_KorisnikIdAndProizvod_ProizvodId(Long kupacId, Long proizvodId);
    boolean existsByKupac_KorisnikIdAndProizvod_ProizvodId(Long kupacId, Long proizvodId);
    void deleteByKupac_KorisnikIdAndProizvod_ProizvodId(Long kupacId, Long proizvodId);
}