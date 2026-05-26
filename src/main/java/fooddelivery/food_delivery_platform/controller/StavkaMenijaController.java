package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.service.StavkaMenijaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/stavke-menija")
public class StavkaMenijaController {

    @Autowired
    private StavkaMenijaService stavkaMenijaService;


    @GetMapping("/meni/{meniId}")
    public ResponseEntity<List<StavkaMenija>> getItemsByMenu(@PathVariable Long meniId) {
        return ResponseEntity.ok(stavkaMenijaService.getItemsByMenu(meniId));
    }

}