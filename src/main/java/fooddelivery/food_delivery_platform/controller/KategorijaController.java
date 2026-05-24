package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Kategorija;
import fooddelivery.food_delivery_platform.repository.KategorijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kategorije")
@RequiredArgsConstructor
public class KategorijaController {

    private final KategorijaRepository kategorijaRepository;

    @GetMapping
    public ResponseEntity<List<Kategorija>> getAll() {
        return ResponseEntity.ok(kategorijaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kategorija> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kategorijaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorija nije pronađena")));
    }

    @PostMapping
    public ResponseEntity<Kategorija> create(@RequestBody Kategorija kategorija) {
        return ResponseEntity.ok(kategorijaRepository.save(kategorija));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kategorijaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}