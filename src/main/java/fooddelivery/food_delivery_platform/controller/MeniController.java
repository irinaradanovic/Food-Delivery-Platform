package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.RestoranRepository;
import fooddelivery.food_delivery_platform.service.MeniService;
import fooddelivery.food_delivery_platform.service.RestoranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menadzer")
@CrossOrigin(origins = "*")
public class MeniController {

    @Autowired
    private RestoranService restoranService;
    @Autowired
    private MeniService meniService;

    // svi restorani kojima ovaj menadzer upravlja
    @GetMapping("/restorani")
    public List<Restoran> getRestaurantsForManager(@RequestParam Long menadzerId) {
        return restoranService.findByMenadzerKorisnikId(menadzerId);
    }

    // svi meniji za izabrani restoran
    @GetMapping("/restorani/{restoranId}/meniji")
    public List<Meni> getMenusForRestaurant(@PathVariable Long restoranId) {
        return meniService.findByRestoranRestoranId(restoranId);
    }
}