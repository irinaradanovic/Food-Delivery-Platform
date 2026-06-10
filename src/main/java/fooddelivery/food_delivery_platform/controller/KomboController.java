package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.KomboRequestDTO;
import fooddelivery.food_delivery_platform.dto.KomboResultDTO;
import fooddelivery.food_delivery_platform.service.KomboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kombo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KomboController {

    private final KomboService komboService;

    @PostMapping("/predlozi")
    public ResponseEntity<List<KomboResultDTO>> predloziKomboe(
            @RequestBody KomboRequestDTO request) {
        return ResponseEntity.ok(komboService.predloziKomboe(request));
    }
}