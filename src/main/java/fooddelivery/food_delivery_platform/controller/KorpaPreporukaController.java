package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.KorpaPreporukaRequestDTO;
import fooddelivery.food_delivery_platform.dto.KorpaPreporukaResponseDTO;
import fooddelivery.food_delivery_platform.model.Korisnik;
import fooddelivery.food_delivery_platform.model.PrikazanaPreporuka;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import fooddelivery.food_delivery_platform.repository.PrikazanaPreporukaRepository;
import fooddelivery.food_delivery_platform.repository.ProizvodRepository;
import fooddelivery.food_delivery_platform.service.KorpaPreporukaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/korpa-preporuke")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KorpaPreporukaController {

    private final KorpaPreporukaService korpaPreporukaService;
    private final PrikazanaPreporukaRepository prikazanaRepo;
    private final KorisnikRepository korisnikRepository;
    private final ProizvodRepository proizvodRepository;

    @PostMapping
    public ResponseEntity<KorpaPreporukaResponseDTO> getKorpaPreporuke(
            @RequestBody KorpaPreporukaRequestDTO request) {

        KorpaPreporukaResponseDTO response = korpaPreporukaService.getPreporuke(request);

        // Snimi prikazane korpa-preporuke ako je kupac poznat
        if (request.getKupacId() != null && response.getPreporuke() != null) {
            snimiPrikazane(request.getKupacId(), response.getPreporuke());
        }

        return ResponseEntity.ok(response);
    }

    private void snimiPrikazane(Long kupacId,
                                List<KorpaPreporukaResponseDTO.PreporukaStavkaDTO> stavke) {

        Korisnik kupac = korisnikRepository.findById(kupacId).orElse(null);
        if (kupac == null) return;

        LocalDateTime sada = LocalDateTime.now();
        // Prozor od minut — ako je isti proizvod vec prikazan u ovom periodu, preskoči
        LocalDateTime prozor = sada.minusMinutes(1);

        List<PrikazanaPreporuka> zapisi = stavke.stream()
                .map(s -> {
                    Proizvod p = proizvodRepository.findById(s.getProizvodId()).orElse(null);
                    if (p == null) return null;
                    // Proveri duplikat
                    boolean vecPostoji = prikazanaRepo
                            .existsByKupac_KorisnikIdAndProizvod_ProizvodIdAndTipPreporukeAndPrikazanoUAfter(
                                    kupacId, p.getProizvodId(),
                                    PrikazanaPreporuka.TipPreporuke.KORPA,
                                    prozor);
                    if (vecPostoji) return null;
                    return PrikazanaPreporuka.builder()
                            .kupac(kupac)
                            .proizvod(p)
                            .tipPreporuke(PrikazanaPreporuka.TipPreporuke.KORPA)
                            .prikazanoU(sada)
                            .uspesna(false)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        if (!zapisi.isEmpty()) {
            prikazanaRepo.saveAll(zapisi);
        }
    }
}