package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.OmiljenaKategorija;
import fooddelivery.food_delivery_platform.service.OmiljenaKategorijaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/omiljene-kategorije")
@RequiredArgsConstructor
public class OmiljenaKategorijaController {

    private final OmiljenaKategorijaService omiljenaKategorijaService;

    @GetMapping("/{kupacId}")
    public ResponseEntity<List<OmiljenaKategorija>> getOmiljeneKategorije(@PathVariable Long kupacId) {
        return ResponseEntity.ok(omiljenaKategorijaService.getOmiljeneKategorije(kupacId));
    }

    @PostMapping("/{kupacId}/kategorije/{kategorijaId}")
    public ResponseEntity<OmiljenaKategorija> dodaj(
            @PathVariable Long kupacId,
            @PathVariable Long kategorijaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(omiljenaKategorijaService.dodajOmiljenuKategoriju(kupacId, kategorijaId));
    }

    @DeleteMapping("/{kupacId}/kategorije/{kategorijaId}")
    public ResponseEntity<Void> ukloni(
            @PathVariable Long kupacId,
            @PathVariable Long kategorijaId) {
        omiljenaKategorijaService.ukloniOmiljenuKategoriju(kupacId, kategorijaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{kupacId}/kategorije/{kategorijaId}/check")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable Long kupacId,
            @PathVariable Long kategorijaId) {
        return ResponseEntity.ok(Map.of("jeOmiljena",
                omiljenaKategorijaService.jeOmiljenaKategorija(kupacId, kategorijaId)));
    }
}