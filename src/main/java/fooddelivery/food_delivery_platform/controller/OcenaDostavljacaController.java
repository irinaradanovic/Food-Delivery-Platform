package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.OcenaDostavljacaDTO;
import fooddelivery.food_delivery_platform.model.Dostavljac;
import fooddelivery.food_delivery_platform.model.OcenaDostavljaca;
import fooddelivery.food_delivery_platform.repository.DostavljacRepository;
import fooddelivery.food_delivery_platform.service.OcenaDostavljacaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ocene")
@CrossOrigin
public class OcenaDostavljacaController {

    private final OcenaDostavljacaService ocenaService;
    private final DostavljacRepository dostavljacRepository;

    public OcenaDostavljacaController(OcenaDostavljacaService ocenaService,
                                      DostavljacRepository dostavljacRepository) {
        this.ocenaService = ocenaService;
        this.dostavljacRepository = dostavljacRepository;
    }

    @PostMapping
    public OcenaDostavljaca add(@RequestBody OcenaDostavljacaDTO dto) {

        Dostavljac dostavljac =
                dostavljacRepository.findById(dto.getDostavljacId())
                        .orElseThrow();

        OcenaDostavljaca ocena =
                new OcenaDostavljaca();

        ocena.setDostavljac(dostavljac);
        ocena.setOcena(dto.getOcena());
        ocena.setKomentar(dto.getKomentar());
        ocena.setTipOcene(dto.getTipOcene());

        return ocenaService.dodajOcenu(ocena);
    }
}