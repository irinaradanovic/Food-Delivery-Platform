package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.MeniUpdateDTO;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.RestoranRepository;
import fooddelivery.food_delivery_platform.service.MeniService;
import fooddelivery.food_delivery_platform.service.RestoranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    // azuriranje menija (menadzer)
    @PutMapping("/{id}")
    public ResponseEntity<?> ažurirajMeni(
            @PathVariable Long id,
            @RequestBody MeniUpdateDTO dto,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        try {
            Meni azuriranMeni = meniService.updateMenu(id, dto, trenutniKorisnikId);
            return ResponseEntity.ok(azuriranMeni);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // deaktiviranje menija (menadzer)
    @PutMapping("/{id}/deaktiviraj")
    public ResponseEntity<Void> deaktivirajMeni(@PathVariable Long id,  @RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        meniService.deactivateMenu(id, trenutniKorisnikId);
        return ResponseEntity.noContent().build();
    }
}