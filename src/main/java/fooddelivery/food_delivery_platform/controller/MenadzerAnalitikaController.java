package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.HronikaEksperimenataDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.PokretaciIzmenaDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.StabilnostUpravljanjaDTO;
import fooddelivery.food_delivery_platform.service.MenadzerAnalitikaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menadzer/analitika")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MenadzerAnalitikaController {

    @Autowired
    private MenadzerAnalitikaService analitikaService;

    @GetMapping("/pokretaci")
    public PokretaciIzmenaDTO preuzmiPokretace(@RequestParam("grupniMeniId") Long grupniMeniId) {
        return  analitikaService.getPokretaceIzmena(grupniMeniId);
    }

    @GetMapping("/stabilnost")
    public StabilnostUpravljanjaDTO preuzmiStabilnost(@RequestParam("grupniMeniId") Long grupniMeniId) {
        return analitikaService.getStabilnostUpravljanja(grupniMeniId);
    }

    @GetMapping("/hronika")
    public HronikaEksperimenataDTO preuzmiHroniku(@RequestParam("grupniMeniId") Long grupniMeniId) {
        return analitikaService.getHronikuEksperimenata(grupniMeniId);
    }


}
