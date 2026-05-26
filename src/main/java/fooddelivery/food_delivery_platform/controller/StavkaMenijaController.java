package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.NovaStavkaMenijaDTO;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.service.StavkaMenijaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/stavke-menija")
public class StavkaMenijaController {

    @Autowired
    private StavkaMenijaService stavkaMenijaService;


    @GetMapping("/meni/{meniId}")
    public ResponseEntity<List<StavkaMenija>> getItemsByMenu(@PathVariable Long meniId) {
        return ResponseEntity.ok(stavkaMenijaService.getItemsByMenu(meniId));
    }

    @PostMapping("/meni/{meniId}")
    public ResponseEntity<Void> addMenuItem(
            @PathVariable Long meniId,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @RequestPart("podaci") NovaStavkaMenijaDTO dto,
            @RequestPart(value = "slika", required = false) MultipartFile slika) throws IOException {

        stavkaMenijaService.addMenuItem(meniId, trenutniKorisnikId, dto, slika);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/meni/{meniId}/stavka/{stavkaId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long meniId,
            @PathVariable Long stavkaId,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        try {
            stavkaMenijaService.deleteItem(meniId, stavkaId, trenutniKorisnikId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška: " + e.getMessage());
        }
    }

}