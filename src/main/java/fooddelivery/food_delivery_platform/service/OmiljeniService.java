package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OmiljeniService {

    private final OmiljeniProizvodRepository omiljeniRepo;
    private final KupacRepository kupacRepo;
    private final ProizvodRepository proizvodRepo;

    public List<OmiljeniProizvod> getOmiljeni(Long kupacId) {
        kupacRepo.findById(kupacId)
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + kupacId));
        return omiljeniRepo.findByKupac_KorisnikId(kupacId);
    }

    @Transactional
    public OmiljeniProizvod dodajOmiljeni(Long kupacId, Long proizvodId) {
        if (omiljeniRepo.existsByKupac_KorisnikIdAndProizvod_ProizvodId(kupacId, proizvodId)) {
            throw new RuntimeException("Proizvod je već u omiljenima");
        }
        Kupac kupac = kupacRepo.findById(kupacId)
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + kupacId));
        Proizvod proizvod = proizvodRepo.findById(proizvodId)
                .orElseThrow(() -> new RuntimeException("Proizvod nije pronađen: " + proizvodId));

        return omiljeniRepo.save(OmiljeniProizvod.builder()
                .kupac(kupac)
                .proizvod(proizvod)
                .build());
    }

    @Transactional
    public void ukloniOmiljeni(Long kupacId, Long proizvodId) {
        if (!omiljeniRepo.existsByKupac_KorisnikIdAndProizvod_ProizvodId(kupacId, proizvodId)) {
            throw new RuntimeException("Omiljeni proizvod nije pronađen");
        }
        omiljeniRepo.deleteByKupac_KorisnikIdAndProizvod_ProizvodId(kupacId, proizvodId);
    }

    public boolean jeOmiljeni(Long kupacId, Long proizvodId) {
        return omiljeniRepo.existsByKupac_KorisnikIdAndProizvod_ProizvodId(kupacId, proizvodId);
    }
}