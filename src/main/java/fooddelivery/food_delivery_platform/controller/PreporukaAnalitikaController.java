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


    @GetMapping("/kupac/{kupacId}")
    public ResponseEntity<PreporukaAnalitikaDTO> getAnalitikaZaKupca(
            @PathVariable Long kupacId,
            @RequestParam(defaultValue = "30") int dani) {
        return ResponseEntity.ok(analitikaService.izracunajAnalitiku(kupacId, dani));
    }


    @GetMapping("/svi")
    public ResponseEntity<PreporukaAnalitikaDTO> getAnalitikaZaSve(
            @RequestParam(defaultValue = "30") int dani) {
        return ResponseEntity.ok(analitikaService.izracunajAnalitikunZaSve(dani));
    }
}