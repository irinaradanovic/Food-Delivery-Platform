package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Sastojak;
import fooddelivery.food_delivery_platform.service.MeniService;
import fooddelivery.food_delivery_platform.service.SastojakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sastojci")
@CrossOrigin(origins = "*")
public class SastojakController {

    @Autowired
    private SastojakService sastojakService;

    @GetMapping("/menadzer")
    public ResponseEntity<List<Sastojak>> getIngredientsForManager(@RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        List<Sastojak> sastojci = sastojakService.getIngredientsForManager(trenutniKorisnikId);
        return ResponseEntity.ok(sastojci);
    }
}
