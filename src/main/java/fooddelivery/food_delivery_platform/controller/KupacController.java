package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Kupac;
import fooddelivery.food_delivery_platform.service.KupacService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kupci")
@RequiredArgsConstructor
public class KupacController {

    private final KupacService kupacService;

    @GetMapping
    public ResponseEntity<List<Kupac>> getAll() {
        return ResponseEntity.ok(kupacService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kupac> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kupacService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Kupac> create(@RequestBody Kupac kupac) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kupacService.create(kupac));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kupac> update(@PathVariable Long id, @RequestBody Kupac kupac) {
        return ResponseEntity.ok(kupacService.update(id, kupac));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kupacService.delete(id);
        return ResponseEntity.noContent().build();
    }
}