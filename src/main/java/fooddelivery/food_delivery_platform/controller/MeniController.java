package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.MeniUpdateDTO;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.RestoranRepository;
import fooddelivery.food_delivery_platform.service.MeniService;
import fooddelivery.food_delivery_platform.service.RestoranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meni")
@CrossOrigin(origins = "*")
public class MeniController {

    @Autowired
    private RestoranService restoranService;
    @Autowired
    private MeniService meniService;

    @GetMapping("/{meniId}")
    public Meni getMeni(@PathVariable Long meniId) {
        return meniService.getById(meniId);
    }

    // svi restorani kojima ovaj menadzer upravlja
    @GetMapping("/restorani")
    public List<Restoran> getRestaurantsForManager(@RequestParam Long menadzerId) {
        return restoranService.findByMenadzerKorisnikId(menadzerId);
    }

    // svi meniji za izabrani restoran
    @GetMapping("/restorani/{restoranId}")
    public List<Meni> getMenusForRestaurant(@PathVariable Long restoranId) {
        return meniService.findByRestoranRestoranId(restoranId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(@PathVariable Long id, @RequestBody MeniUpdateDTO dto) {
        try {
            Meni azuriranMeni = meniService.updateMenu(id, dto);
            return ResponseEntity.ok(azuriranMeni);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}