package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.OmiljeniProizvod;
import fooddelivery.food_delivery_platform.service.OmiljeniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/omiljeni")
@RequiredArgsConstructor
public class OmiljeniController {

    private final OmiljeniService omiljeniService;

    @GetMapping("/{kupacId}")
    public ResponseEntity<List<OmiljeniProizvod>> getOmiljeni(@PathVariable Long kupacId) {
        return ResponseEntity.ok(omiljeniService.getOmiljeni(kupacId));
    }

    @PostMapping("/{kupacId}/proizvodi/{proizvodId}")
    public ResponseEntity<OmiljeniProizvod> dodaj(
            @PathVariable Long kupacId,
            @PathVariable Long proizvodId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(omiljeniService.dodajOmiljeni(kupacId, proizvodId));
    }

    @DeleteMapping("/{kupacId}/proizvodi/{proizvodId}")
    public ResponseEntity<Void> ukloni(
            @PathVariable Long kupacId,
            @PathVariable Long proizvodId) {
        omiljeniService.ukloniOmiljeni(kupacId, proizvodId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{kupacId}/proizvodi/{proizvodId}/check")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable Long kupacId,
            @PathVariable Long proizvodId) {
        return ResponseEntity.ok(Map.of("jeOmiljeni", omiljeniService.jeOmiljeni(kupacId, proizvodId)));
    }
}