package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.service.RestoranService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restorani")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RestoranController {

    private final RestoranService restoranService;

    // Svi restorani - za kupca
    @GetMapping
    public ResponseEntity<List<Restoran>> getSviRestorani() {
        return ResponseEntity.ok(restoranService.getSviRestorani());
    }

    // Restorani po menadzeru - za menadzera
    @GetMapping("/menadzer/{menadzerId}")
    public ResponseEntity<List<Restoran>> getRestoranByMenadzer(@PathVariable Long menadzerId) {
        return ResponseEntity.ok(restoranService.findByMenadzerKorisnikId(menadzerId));
    }
}