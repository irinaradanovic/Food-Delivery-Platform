package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.CenovnikMasovniUpdateDTO;
import fooddelivery.food_delivery_platform.dto.IzmenaStavkeMenijaDTO;
import fooddelivery.food_delivery_platform.dto.NovaStavkaMenijaDTO;
import fooddelivery.food_delivery_platform.model.Meni;
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
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/stavke-menija")
public class StavkaMenijaController {

    @Autowired
    private StavkaMenijaService stavkaMenijaService;

    @GetMapping("/{id}")
    public StavkaMenija getStavkaMenija(@PathVariable Long id) {
        return stavkaMenijaService.getItemById(id);
    }

    // menadzer - vidi sve stavke menija
    @GetMapping("/meni/{meniId}")
    public ResponseEntity<List<StavkaMenija>> getItemsByMenu(@PathVariable Long meniId) {
        return ResponseEntity.ok(stavkaMenijaService.getItemsByMenu(meniId));
    }

    // kupac - stavke menija samo iz aktivnog menija
    @GetMapping("/kupac/meni/{meniId}")
    public ResponseEntity<?> getItemsByMenuForKupac(@PathVariable Long meniId) {
        try {
            return ResponseEntity.ok(stavkaMenijaService.getItemsByMenuForKupac(meniId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/kupac/restoran/{restoranId}")
    public ResponseEntity<?> getAktivneStavkeZaRestoranKupac(@PathVariable Long restoranId) {
        return ResponseEntity.ok(stavkaMenijaService.getAktivneStavkeZaRestoranKupac(restoranId));
    }

    @PostMapping("/meni/{meniId}")
    public ResponseEntity<Map<String, Long>> addMenuItem(
            @PathVariable Long meniId,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @RequestPart("podaci") NovaStavkaMenijaDTO dto,
            @RequestPart(value = "slika", required = false) MultipartFile slika) throws IOException {

        Long novaVerzijaMenijaId = stavkaMenijaService.addMenuItem(meniId, trenutniKorisnikId, dto, slika);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("noviMeniId", novaVerzijaMenijaId));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greska: " + e.getMessage());
        }
    }

    @PutMapping("/meni/{meniId}/stavka/{stavkaId}")
    public ResponseEntity<?> azurirajStavku(
            @PathVariable("meniId") Long meniId,
            @PathVariable Long stavkaId,
            @RequestPart("podaci") IzmenaStavkeMenijaDTO dto,
            @RequestPart(value = "slika", required = false) MultipartFile slika,
            @RequestHeader("X-User-Id") Long korisnikId) {

        try {
            stavkaMenijaService.updateMenuItem(meniId, stavkaId, dto, slika, korisnikId);
            return ResponseEntity.ok().body("Stavka je uspesno izmenjena.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/meni/{meniId}/masovni-cenovnik")
    public ResponseEntity<Meni> updateMenuPriceList(
            @PathVariable Long meniId,
            @RequestBody CenovnikMasovniUpdateDTO dto,
            @RequestHeader("X-User-Id") Long korisnikId) {
        try {
            Meni noviMeni= stavkaMenijaService.updateMenuPriceListWithVersioning(meniId, dto, korisnikId);
            return ResponseEntity.ok(noviMeni);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/preporuka-vremena/{kategorijaId}")
    public ResponseEntity<Map<String, Integer>> getPreporukaVremena(@PathVariable Long kategorijaId) {
        Map<String, Integer> preporuka = stavkaMenijaService.calculateAvgPreparationTime(kategorijaId);
        // vraca min:5, max:10 npr
        return ResponseEntity.ok(preporuka);
    }

}
