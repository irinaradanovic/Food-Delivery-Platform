package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.HronikaEksperimenataDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.IstorijaCeneStavkeDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.KomparacijaVerzijaDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.PokretaciIzmenaDTO;
import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.StabilnostUpravljanjaDTO;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import fooddelivery.food_delivery_platform.service.MenadzerAnalitikaService;
import fooddelivery.food_delivery_platform.service.PorudzbineAnalitikaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menadzer/analitika")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MenadzerAnalitikaController {

    @Autowired
    private MenadzerAnalitikaService analitikaService;

    @Autowired
    private PorudzbineAnalitikaService porudzbineAnalitikaService;

    @Autowired
    private StavkaMenijaRepository stavkaMenijaRepository;

    @GetMapping("/pokretaci")
    public PokretaciIzmenaDTO preuzmiPokretace(@RequestParam("grupniMeniId") Long grupniMeniId) {
        return analitikaService.getPokretaceIzmena(grupniMeniId);
    }

    @GetMapping("/stabilnost")
    public StabilnostUpravljanjaDTO preuzmiStabilnost(@RequestParam("grupniMeniId") Long grupniMeniId) {
        return analitikaService.getStabilnostUpravljanja(grupniMeniId);
    }

    @GetMapping("/hronika")
    public HronikaEksperimenataDTO preuzmiHroniku(@RequestParam("grupniMeniId") Long grupniMeniId) {
        return analitikaService.getHronikuEksperimenata(grupniMeniId);
    }

    @GetMapping("/komparacija")
    public ResponseEntity<KomparacijaVerzijaDTO> getKomparacijaVerzija(
            @RequestParam Long meniIdA,
            @RequestParam Long meniIdB) {
        KomparacijaVerzijaDTO komparacija = analitikaService.komparacijaVerzija(meniIdA, meniIdB);
        return ResponseEntity.ok(komparacija);
    }

    @GetMapping("/cena-stavke")
    public ResponseEntity<List<IstorijaCeneStavkeDTO>> getIstorijaCenaStavke(
            @RequestParam Long grupniMeniId,
            @RequestParam Long proizvodId) {
        List<StavkaMenija> stavke = stavkaMenijaRepository.findIstorijaCenaStavke(grupniMeniId, proizvodId);
        List<IstorijaCeneStavkeDTO> istorija = stavke.stream()
                .map(sm -> new IstorijaCeneStavkeDTO(sm.getMeni().getVerzija(), sm.getCena()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(istorija);
    }

    @GetMapping("/porudzbine")
    public ResponseEntity<?> getAnalitikaPorudzbina(
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @RequestParam(required = false) LocalDate od,
            @RequestParam(name = "do", required = false) LocalDate datumDo,
            @RequestParam(required = false) Long restoranId) {
        try {
            return ResponseEntity.ok(porudzbineAnalitikaService.getAnalitika(trenutniKorisnikId, od, datumDo, restoranId));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }
}
