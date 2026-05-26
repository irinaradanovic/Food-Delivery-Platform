package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.KreiranjePorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.PromenaStatusaPorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.PromenaStatusaPorudzbineResponseDTO;
import fooddelivery.food_delivery_platform.dto.StatusPorudzbineIstorijaDTO;
import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.StatusPorudzbineIstorija;
import fooddelivery.food_delivery_platform.service.PorudzbinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/porudzbine")
public class PorudzbinaController {

    private final PorudzbinaService porudzbinaService;

    @GetMapping
    public ResponseEntity<List<Porudzbina>> getAll(
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @RequestParam(required = false) Long kupacId
    ) {
        return ResponseEntity.ok(porudzbinaService.getAll(trenutniKorisnikId, kupacId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId
    ) {
        try {
            return ResponseEntity.ok(porudzbinaService.getById(trenutniKorisnikId, id));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("greska", e.getMessage()));
        }
    }

    @GetMapping("/{id}/istorija-statusa")
        public ResponseEntity<?> getStatusHistory(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId
        ) {
        try {
            List<StatusPorudzbineIstorija> istorija = porudzbinaService.getStatusHistory(trenutniKorisnikId, id);
            List<StatusPorudzbineIstorijaDTO> response = istorija.stream()
                    .map(item -> StatusPorudzbineIstorijaDTO.builder()
                            .statusIstorijaId(item.getStatusIstorijaId())
                            .porudzbinaId(item.getPorudzbina().getPorudzbinaId())
                            .status(item.getStatus())
                            .vremePromene(item.getVremePromene())
                            .promenioKorisnikId(trenutniKorisnikId)
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("greska", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @Valid @RequestBody KreiranjePorudzbineDTO dto
    ) {
        try {
            Porudzbina nova = porudzbinaService.create(trenutniKorisnikId, dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "poruka", "Porudzbina kreirana",
                    "dostavljacId", nova.getDostavljacId()
                ));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> promeniStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @Valid @RequestBody PromenaStatusaPorudzbineDTO dto
    ) {
        try {
            Porudzbina azurirana = porudzbinaService.promeniStatus(trenutniKorisnikId, id, dto);
            PromenaStatusaPorudzbineResponseDTO response = PromenaStatusaPorudzbineResponseDTO.builder()
                .porudzbinaId(azurirana.getPorudzbinaId())
                .status(azurirana.getStatus())
                .vremePromene(azurirana.getIstorijaStatusa().isEmpty()
                    ? null
                    : azurirana.getIstorijaStatusa().get(azurirana.getIstorijaStatusa().size() - 1).getVremePromene())
                .promenioKorisnikId(trenutniKorisnikId)
                .build();
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }
}