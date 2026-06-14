package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.MeniUpdateDTO;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.service.MeniService;
import fooddelivery.food_delivery_platform.service.RestoranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;

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

    // svi meniji za izabrani restoran (menadzer - vidi sve)
    @GetMapping("/restorani/{restoranId}")
    public List<Meni> getMenusForRestaurant(@PathVariable Long restoranId) {
        return meniService.findByRestoranRestoranId(restoranId);
    }

    // samo aktivni meniji za izabrani restoran (kupac)
    @GetMapping("/kupac/restorani/{restoranId}")
    public List<Meni> getActiveMenusForKupac(@PathVariable Long restoranId) {
        return meniService.findAktivniByRestoranRestoranId(restoranId);
    }

    // azuriranje menija (menadzer)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(
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
    public ResponseEntity<Void> deactivateMenu(@PathVariable Long id, @RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        meniService.deactivateMenu(id, trenutniKorisnikId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> createNewMenu(
            @RequestBody Meni noviMeni,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        try {
            Meni kreiranMeni = meniService.createMenu(noviMeni, trenutniKorisnikId);
            return ResponseEntity.status(HttpStatus.CREATED).body(kreiranMeni);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greska na serveru: " + e.getMessage());
        }
    }

    @GetMapping("/grupa/{grupniId}")
    public List<Meni> getMenuVersionHstory(@PathVariable Long grupniId) {
        return meniService.getMenuVersionHstory(grupniId);
    }

    @PutMapping("/{meniId}/vrati-verziju")
    public ResponseEntity<Map<String, Long>> rollbackToVersion(
            @PathVariable Long meniId,
            @RequestHeader("X-User-Id") Long userId) {
        Meni novaVerzija = meniService.rollbackToVersion(meniId, userId);
        return ResponseEntity.ok(Map.of("noviMeniId", novaVerzija.getMeniId()));
    }
}