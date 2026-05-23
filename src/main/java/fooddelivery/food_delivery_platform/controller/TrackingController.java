package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/klik/{kupacId}/proizvodi/{proizvodId}")
    public ResponseEntity<Klik> zabeleziKlik(
            @PathVariable Long kupacId,
            @PathVariable Long proizvodId,
            @RequestBody(required = false) Map<String, String> body) {
        String tipAkcije = body != null ? body.getOrDefault("tipAkcije", "PREGLED") : "PREGLED";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trackingService.zabeleziKlik(kupacId, proizvodId, tipAkcije));
    }

    @PostMapping("/pretraga/{kupacId}")
    public ResponseEntity<Pretraga> zabeleziPretragu(
            @PathVariable Long kupacId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trackingService.zabeleziPretragu(
                        kupacId,
                        body.get("tekstUpita"),
                        body.getOrDefault("tipPretrage", "OPSTA")));
    }

    @GetMapping("/klikovi/{kupacId}")
    public ResponseEntity<List<Klik>> getKlikovi(@PathVariable Long kupacId) {
        return ResponseEntity.ok(trackingService.getKlikovi(kupacId));
    }

    @GetMapping("/pretrage/{kupacId}")
    public ResponseEntity<List<Pretraga>> getPretrage(@PathVariable Long kupacId) {
        return ResponseEntity.ok(trackingService.getPretrage(kupacId));
    }
}