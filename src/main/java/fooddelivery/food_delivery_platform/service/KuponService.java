package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.KreiranjeKuponaDTO;
import fooddelivery.food_delivery_platform.dto.KuponDTO;
import fooddelivery.food_delivery_platform.model.Korisnik;
import fooddelivery.food_delivery_platform.model.Kupon;
import fooddelivery.food_delivery_platform.model.Kupac;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import fooddelivery.food_delivery_platform.repository.KuponRepository;
import fooddelivery.food_delivery_platform.repository.KupacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KuponService {

    private final KuponRepository kuponRepository;
    private final KupacRepository kupacRepository;
    private final KorisnikRepository korisnikRepository;

    @Transactional
    public KuponDTO kreiraj(Long trenutniKorisnikId, KreiranjeKuponaDTO dto) {
        requireAdmin(trenutniKorisnikId);
        if (!dto.getVaziDo().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Rok vazenja kupona mora biti u buducnosti.");
        }

        Kupac kupac = kupacRepository.findById(dto.getKupacId())
                .orElseThrow(() -> new RuntimeException("Kupac nije pronadjen: " + dto.getKupacId()));

        Kupon kupon = Kupon.builder()
                .kod(generisiKod())
                .popustIznos(dto.getVrednost().setScale(2, RoundingMode.HALF_UP))
                .popustProcenat(null)
                .vaziOd(LocalDateTime.now())
                .vaziDo(dto.getVaziDo())
                .aktivan(true)
                .maxUpotreba(1)
                .upotrebljenoPuta(0)
                .vlasnik(kupac)
                .build();

        return toDto(kuponRepository.save(kupon));
    }

    @Transactional(readOnly = true)
    public List<KuponDTO> getAll(Long trenutniKorisnikId) {
        requireAdmin(trenutniKorisnikId);
        return kuponRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<KuponDTO> getMoji(Long trenutniKorisnikId) {
        Korisnik korisnik = korisnikRepository.findById(trenutniKorisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronadjen: " + trenutniKorisnikId));
        if (korisnik.getUloga() == null || !korisnik.getUloga().equalsIgnoreCase("KUPAC")) {
            throw new AccessDeniedException("Samo kupac moze da pregleda svoje kupone.");
        }
        LocalDateTime sada = LocalDateTime.now();
        return kuponRepository.findByVlasnik_KorisnikId(trenutniKorisnikId).stream()
                .filter(Kupon::isAktivan)
                .filter(kupon -> kupon.getVaziOd() == null || !kupon.getVaziOd().isAfter(sada))
                .filter(kupon -> kupon.getVaziDo() == null || !kupon.getVaziDo().isBefore(sada))
                .filter(kupon -> !iskoriscen(kupon))
                .map(this::toDto)
                .toList();
    }

    private void requireAdmin(Long korisnikId) {
        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronadjen: " + korisnikId));
        if (korisnik.getUloga() == null || !korisnik.getUloga().equalsIgnoreCase("ADMIN")) {
            throw new AccessDeniedException("Samo administrator moze da upravlja kuponima.");
        }
    }

    private String generisiKod() {
        String kod;
        do {
            kod = "KUPON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (kuponRepository.findByKod(kod).isPresent());
        return kod;
    }

    private KuponDTO toDto(Kupon kupon) {
        Kupac kupac = kupon.getVlasnik();
        return KuponDTO.builder()
                .kuponId(kupon.getKuponId())
                .kod(kupon.getKod())
                .vrednost(kupon.getPopustIznos())
                .vaziOd(kupon.getVaziOd())
                .vaziDo(kupon.getVaziDo())
                .aktivan(kupon.isAktivan())
                .iskoriscen(iskoriscen(kupon))
                .kupacId(kupac != null ? kupac.getKorisnikId() : null)
                .kupacIme(kupac != null ? kupac.getIme() + " " + kupac.getPrezime() : null)
                .kupacEmail(kupac != null ? kupac.getEmail() : null)
                .build();
    }

    private boolean iskoriscen(Kupon kupon) {
        return kupon.getMaxUpotreba() != null
                && kupon.getUpotrebljenoPuta() != null
                && kupon.getUpotrebljenoPuta() >= kupon.getMaxUpotreba();
    }
}
