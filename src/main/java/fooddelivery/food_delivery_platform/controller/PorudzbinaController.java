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
    public ResponseEntity<List<Porudzbina>> getAll(@RequestParam(required = false) Long kupacId) {
        if (kupacId != null) {
            return ResponseEntity.ok(porudzbinaService.getByKupac(kupacId));
        }
        return ResponseEntity.ok(porudzbinaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(porudzbinaService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("greska", e.getMessage()));
        }
    }

    @GetMapping("/{id}/istorija-statusa")
    public ResponseEntity<?> getStatusHistory(@PathVariable Long id) {
        try {
            List<StatusPorudzbineIstorija> istorija = porudzbinaService.getStatusHistory(id);
            List<StatusPorudzbineIstorijaDTO> response = istorija.stream()
                    .map(item -> StatusPorudzbineIstorijaDTO.builder()
                            .statusIstorijaId(item.getStatusIstorijaId())
                            .porudzbinaId(item.getPorudzbina().getPorudzbinaId())
                            .status(item.getStatus())
                            .vremePromene(item.getVremePromene())
                            .promenioKorisnikId(item.getPromenioKorisnikId())
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("greska", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody KreiranjePorudzbineDTO dto) {
        try {
            porudzbinaService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("poruka", "Porudzbina kreirana"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> promeniStatus(
            @PathVariable Long id,
            @Valid @RequestBody PromenaStatusaPorudzbineDTO dto
    ) {
        try {
            Porudzbina azurirana = porudzbinaService.promeniStatus(id, dto);
            PromenaStatusaPorudzbineResponseDTO response = PromenaStatusaPorudzbineResponseDTO.builder()
                .porudzbinaId(azurirana.getPorudzbinaId())
                .status(azurirana.getStatus())
                .vremePromene(azurirana.getIstorijaStatusa().isEmpty()
                    ? null
                    : azurirana.getIstorijaStatusa().get(azurirana.getIstorijaStatusa().size() - 1).getVremePromene())
                .promenioKorisnikId(dto.getPromenioKorisnikId())
                .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }
}