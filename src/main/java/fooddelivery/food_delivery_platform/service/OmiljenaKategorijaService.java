package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OmiljenaKategorijaService {

    private final OmiljenaKategorijaRepository omiljenaKategorijaRepo;
    private final KupacRepository kupacRepo;
    private final KategorijaRepository kategorijaRepo;

    public List<OmiljenaKategorija> getOmiljeneKategorije(Long kupacId) {
        kupacRepo.findById(kupacId)
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + kupacId));
        return omiljenaKategorijaRepo.findByKupac_KorisnikId(kupacId);
    }

    @Transactional
    public OmiljenaKategorija dodajOmiljenuKategoriju(Long kupacId, Long kategorijaId) {
        if (omiljenaKategorijaRepo.existsByKupac_KorisnikIdAndKategorija_KategorijaId(kupacId, kategorijaId)) {
            throw new RuntimeException("Kategorija je već u omiljenima");
        }
        Kupac kupac = kupacRepo.findById(kupacId)
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + kupacId));
        Kategorija kategorija = kategorijaRepo.findById(kategorijaId)
                .orElseThrow(() -> new RuntimeException("Kategorija nije pronađena: " + kategorijaId));

        return omiljenaKategorijaRepo.save(OmiljenaKategorija.builder()
                .kupac(kupac)
                .kategorija(kategorija)
                .build());
    }

    @Transactional
    public void ukloniOmiljenuKategoriju(Long kupacId, Long kategorijaId) {
        if (!omiljenaKategorijaRepo.existsByKupac_KorisnikIdAndKategorija_KategorijaId(kupacId, kategorijaId)) {
            throw new RuntimeException("Omiljena kategorija nije pronađena");
        }
        omiljenaKategorijaRepo.deleteByKupac_KorisnikIdAndKategorija_KategorijaId(kupacId, kategorijaId);
    }

    public boolean jeOmiljenaKategorija(Long kupacId, Long kategorijaId) {
        return omiljenaKategorijaRepo.existsByKupac_KorisnikIdAndKategorija_KategorijaId(kupacId, kategorijaId);
    }
}