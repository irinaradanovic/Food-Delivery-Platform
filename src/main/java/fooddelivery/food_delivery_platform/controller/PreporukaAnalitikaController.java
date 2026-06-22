package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.PreporukaAnalitikaDTO;
import fooddelivery.food_delivery_platform.service.PreporukaAnalitikaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preporuke/analitika")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PreporukaAnalitikaController {

    private final PreporukaAnalitikaService analitikaService;

    /**
     * GET /api/preporuke/analitika/kupac/{kupacId}?dani=30
     *
     * Čita iz prikazane_preporuke — tačno šta je prikazano kupcu
     * i da li je naručio. Nema rekonstrukcije.
     */
    @GetMapping("/kupac/{kupacId}")
    public ResponseEntity<PreporukaAnalitikaDTO> getAnalitika(
            @PathVariable Long kupacId,
            @RequestParam(defaultValue = "30") int dani) {
        return ResponseEntity.ok(analitikaService.izracunajAnalitiku(kupacId, dani));
    }
}