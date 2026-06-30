package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.KreiranjeKuponaDTO;
import fooddelivery.food_delivery_platform.dto.KuponDTO;
import fooddelivery.food_delivery_platform.service.KuponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/kuponi")
public class KuponController {

    private final KuponService kuponService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        try {
            List<KuponDTO> kuponi = kuponService.getAll(trenutniKorisnikId);
            return ResponseEntity.ok(kuponi);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }

    @GetMapping("/moji")
    public ResponseEntity<?> getMoji(@RequestHeader("X-User-Id") Long trenutniKorisnikId) {
        try {
            List<KuponDTO> kuponi = kuponService.getMoji(trenutniKorisnikId);
            return ResponseEntity.ok(kuponi);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @Valid @RequestBody KreiranjeKuponaDTO dto
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(kuponService.kreiraj(trenutniKorisnikId, dto));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }
}
