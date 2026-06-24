package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.KomboAnalitikaDTO;
import fooddelivery.food_delivery_platform.service.KomboAnalitikaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kombo/analitika")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KomboAnalitikaController {

    private final KomboAnalitikaService analitikaService;

    @GetMapping("/kupac/{kupacId}")
    public ResponseEntity<KomboAnalitikaDTO> getZaKupca(
            @PathVariable Long kupacId,
            @RequestParam(defaultValue = "30") int dani) {
        return ResponseEntity.ok(analitikaService.izracunajZaKupca(kupacId, dani));
    }

    @GetMapping("/svi")
    public ResponseEntity<KomboAnalitikaDTO> getZaSve(
            @RequestParam(defaultValue = "30") int dani) {
        return ResponseEntity.ok(analitikaService.izracunajZaSve(dani));
    }
}