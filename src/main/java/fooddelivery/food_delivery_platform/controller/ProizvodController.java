package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.MeniProizvodiDTO;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.service.ProizvodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/proizvodi")
@RequiredArgsConstructor
public class ProizvodController {

    private final ProizvodService proizvodService;

    @GetMapping
    public ResponseEntity<List<Proizvod>> getAll() {
        return ResponseEntity.ok(proizvodService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proizvod> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proizvodService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Proizvod>> search(@RequestParam String naziv) {
        return ResponseEntity.ok(proizvodService.search(naziv));
    }

    @PostMapping
    public ResponseEntity<Proizvod> create(@RequestBody Proizvod proizvod) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proizvodService.create(proizvod));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proizvod> update(@PathVariable Long id, @RequestBody Proizvod proizvod) {
        return ResponseEntity.ok(proizvodService.update(id, proizvod));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proizvodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/kategorija/{kategorijaId}")
    public ResponseEntity<List<Proizvod>> getByKategorija(@PathVariable Long kategorijaId) {
        return ResponseEntity.ok(proizvodService.getByKategorija(kategorijaId));
    }

    // Kupac - svi proizvodi iz aktivnih menija restorana sa meni info
    @GetMapping("/kupac/restoran/{restoranId}")
    public ResponseEntity<List<MeniProizvodiDTO>> getProizvodiZaKupca(@PathVariable Long restoranId) {
        return ResponseEntity.ok(proizvodService.getProizvodiZaKupca(restoranId));
    }

    // Kupac - pretraga po nazivu unutar aktivnih menija restorana
    @GetMapping("/kupac/restoran/{restoranId}/search")
    public ResponseEntity<List<Proizvod>> searchProizvodiZaKupca(
            @PathVariable Long restoranId,
            @RequestParam String naziv) {
        return ResponseEntity.ok(proizvodService.searchProizvodiZaKupca(restoranId, naziv));
    }

    // Kupac - filtriranje po kategoriji unutar aktivnih menija restorana
    @GetMapping("/kupac/restoran/{restoranId}/kategorija/{kategorijaId}")
    public ResponseEntity<List<Proizvod>> getProizvodiZaKupcaByKategorija(
            @PathVariable Long restoranId,
            @PathVariable Long kategorijaId) {
        return ResponseEntity.ok(proizvodService.getProizvodiZaKupcaByKategorija(restoranId, kategorijaId));
    }
}