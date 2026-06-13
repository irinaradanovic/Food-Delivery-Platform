package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.service.PreporukaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preporuke")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PreporukaController {

    private final PreporukaService preporukaService;

    // Personalizovane preporuke za kupca
    @GetMapping("/kupac/{kupacId}")
    public ResponseEntity<List<Proizvod>> getPreporuke(
            @PathVariable Long kupacId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(preporukaService.getPersonalizovanePreporuke(kupacId, limit));
    }

    // Sezonske preporuke - proizvodi iz aktivnih SezonskiMeni čija sezona je trenutna
    @GetMapping("/sezonske")
    public ResponseEntity<List<Proizvod>> getSezonskiProizvodi(
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(preporukaService.getSezonskiProizvodi(limit));
    }

    // Vremenske preporuke - proizvodi iz aktivnih VremenskiMeni čiji interval pokriva trenutno vreme
    @GetMapping("/vremenski")
    public ResponseEntity<List<Proizvod>> getVremenskiProizvodi(
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(preporukaService.getVremenskiProizvodi(limit));
    }

    // Trend preporuke - najklikavaniji proizvodi u poslednjih 7 dana
    @GetMapping("/trend")
    public ResponseEntity<List<Proizvod>> getTrendProizvodi(
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(preporukaService.getTrendProizvodi(limit));
    }
}