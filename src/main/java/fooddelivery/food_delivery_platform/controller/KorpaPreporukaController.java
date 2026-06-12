package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.KorpaPreporukaRequestDTO;
import fooddelivery.food_delivery_platform.dto.KorpaPreporukaResponseDTO;
import fooddelivery.food_delivery_platform.service.KorpaPreporukaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/korpa-preporuke")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KorpaPreporukaController {

    private final KorpaPreporukaService korpaPreporukaService;

    @PostMapping
    public ResponseEntity<KorpaPreporukaResponseDTO> getKorpaPreporuke(
            @RequestBody KorpaPreporukaRequestDTO request) {
        return ResponseEntity.ok(korpaPreporukaService.getPreporuke(request));
    }
}