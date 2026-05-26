package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Korisnik;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private final KorisnikRepository korisnikRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String lozinka = credentials.get("lozinka");

        Optional<Korisnik> korisnikOpt = korisnikRepository.findByEmail(email);

        if (korisnikOpt.isPresent()) {
            Korisnik korisnik = korisnikOpt.get();
            if (korisnik.getLozinka().equals(lozinka)) {
                return ResponseEntity.ok(korisnik);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("poruka", "Pogrešan email ili lozinka!"));
    }
}