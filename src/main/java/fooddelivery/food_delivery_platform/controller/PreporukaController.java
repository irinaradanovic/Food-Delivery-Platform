package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Korisnik;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.model.PrikazanaPreporuka;
import fooddelivery.food_delivery_platform.model.PrikazanaPreporuka.TipPreporuke;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import fooddelivery.food_delivery_platform.repository.PrikazanaPreporukaRepository;
import fooddelivery.food_delivery_platform.service.PreporukaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/preporuke")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PreporukaController {

    private final PreporukaService preporukaService;
    private final PrikazanaPreporukaRepository prikazanaRepo;
    private final KorisnikRepository korisnikRepository;

    @GetMapping("/kupac/{kupacId}")
    public ResponseEntity<List<Proizvod>> getPreporuke(
            @PathVariable Long kupacId,
            @RequestParam(defaultValue = "8") int limit) {

        List<Proizvod> preporuke = preporukaService.getPersonalizovanePreporuke(kupacId, limit);
        snimiPrikazane(preporuke, kupacId, TipPreporuke.PERSONALIZOVANA);
        return ResponseEntity.ok(preporuke);
    }

    @GetMapping("/sezonske")
    public ResponseEntity<List<Proizvod>> getSezonskiProizvodi(
            @RequestParam(defaultValue = "8") int limit,
            @RequestParam(required = false) Long kupacId) {

        List<Proizvod> preporuke = preporukaService.getSezonskiProizvodi(limit);
        snimiPrikazane(preporuke, kupacId, TipPreporuke.SEZONSKA);
        return ResponseEntity.ok(preporuke);
    }

    @GetMapping("/vremenski")
    public ResponseEntity<List<Proizvod>> getVremenskiProizvodi(
            @RequestParam(defaultValue = "8") int limit,
            @RequestParam(required = false) Long kupacId) {

        List<Proizvod> preporuke = preporukaService.getVremenskiProizvodi(limit);
        snimiPrikazane(preporuke, kupacId, TipPreporuke.VREMENSKA);
        return ResponseEntity.ok(preporuke);
    }

    @GetMapping("/trend")
    public ResponseEntity<List<Proizvod>> getTrendProizvodi(
            @RequestParam(defaultValue = "8") int limit,
            @RequestParam(required = false) Long kupacId) {

        List<Proizvod> preporuke = preporukaService.getTrendProizvodi(limit);
        snimiPrikazane(preporuke, kupacId, TipPreporuke.TREND);
        return ResponseEntity.ok(preporuke);
    }

    private void snimiPrikazane(List<Proizvod> proizvodi, Long kupacId, TipPreporuke tip) {
        if (proizvodi == null || proizvodi.isEmpty()) return;

        Korisnik kupac = null;
        if (kupacId != null) {
            kupac = korisnikRepository.findById(kupacId).orElse(null);
        }

        LocalDateTime sada = LocalDateTime.now();
        final Korisnik finalKupac = kupac;

        List<PrikazanaPreporuka> zapisi = proizvodi.stream()
                .map(p -> PrikazanaPreporuka.builder()
                        .kupac(finalKupac)
                        .proizvod(p)
                        .tipPreporuke(tip)
                        .prikazanoU(sada)
                        .uspesna(false)
                        .build())
                .toList();

        prikazanaRepo.saveAll(zapisi);
    }
}