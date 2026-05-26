package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.DostavljacDTO;
import fooddelivery.food_delivery_platform.dto.LokacijaDTO;
import fooddelivery.food_delivery_platform.model.Dostavljac;
import fooddelivery.food_delivery_platform.service.DostavljacService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dostavljaci")
@CrossOrigin
public class DostavljacController {

    private final DostavljacService dostavljacService;

    public DostavljacController(DostavljacService dostavljacService) {
        this.dostavljacService = dostavljacService;
    }

    @GetMapping
    public List<DostavljacDTO> getAll() {

        return dostavljacService.getAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public DostavljacDTO create(@RequestBody Dostavljac dostavljac) {

        return convertToDTO(
                dostavljacService.save(dostavljac)
        );
    }

    @PutMapping("/{id}/lokacija")
    public DostavljacDTO updateLocation(@PathVariable Long id,
                                        @RequestBody LokacijaDTO dto) {

        return convertToDTO(
                dostavljacService.updateLocation(
                        id,
                        dto.getLatitude(),
                        dto.getLongitude()
                )
        );
    }

    private DostavljacDTO convertToDTO(Dostavljac d) {

        DostavljacDTO dto = new DostavljacDTO();

        dto.setId(d.getId());
        dto.setIme(d.getIme());
        dto.setPrezime(d.getPrezime());
        dto.setTelefon(d.getTelefon());
        dto.setTrenutnaLat(d.getTrenutnaLat());
        dto.setTrenutnaLng(d.getTrenutnaLng());
        dto.setStatus(d.getStatus());
        dto.setProsecnaOcena(d.getProsecnaOcena());
        dto.setBrojDostava(d.getBrojDostava());

        return dto;
    }
}
