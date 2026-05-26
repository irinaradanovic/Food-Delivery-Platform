package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.DostavaDTO;
import fooddelivery.food_delivery_platform.model.Dostava;
import fooddelivery.food_delivery_platform.model.StatusDostave;
import fooddelivery.food_delivery_platform.service.DostavaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dostave")
@CrossOrigin
public class DostavaController {

    private final DostavaService dostavaService;

    public DostavaController(DostavaService dostavaService) {
        this.dostavaService = dostavaService;
    }

    @GetMapping
    public List<DostavaDTO> getAll() {

        return dostavaService.sveDostave()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public Dostava create(@RequestBody Dostava dostava) {

        return dostavaService.kreirajDostavu(dostava);
    }

    @PutMapping("/{dostavaId}/dodeli/{dostavljacId}")
    public Dostava dodeli(@PathVariable Long dostavaId,
                          @PathVariable Long dostavljacId) {

        return dostavaService.dodeliDostavljaca(
                dostavaId,
                dostavljacId
        );
    }

    @PutMapping("/{id}/status")
    public Dostava promeniStatus(@PathVariable Long id,
                                 @RequestParam StatusDostave status) {

        return dostavaService.promeniStatus(id, status);
    }

    private DostavaDTO convertToDTO(Dostava d) {

        DostavaDTO dto = new DostavaDTO();

        dto.setId(d.getId());
        dto.setPorudzbinaId(d.getPorudzbinaId());

        if(d.getDostavljac() != null) {
            dto.setDostavljacId(d.getDostavljac().getId());
        }

        dto.setAdresaPreuzimanja(d.getAdresaPreuzimanja());
        dto.setAdresaIsporuke(d.getAdresaIsporuke());
        dto.setVremeKreiranja(d.getVremeKreiranja());
        dto.setVremeIsporuke(d.getVremeIsporuke());
        dto.setProcenjenoVreme(d.getProcenjenoVreme());
        dto.setStatus(d.getStatus());

        return dto;
    }
}
