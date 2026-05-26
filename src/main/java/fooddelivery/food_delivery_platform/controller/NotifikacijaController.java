package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.NotifikacijaDostavljaca;
import fooddelivery.food_delivery_platform.repository.NotifikacijaDostavljacaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifikacije")
@CrossOrigin
public class NotifikacijaController {

    private final NotifikacijaDostavljacaRepository repository;

    public NotifikacijaController(NotifikacijaDostavljacaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{dostavljacId}")
    public List<NotifikacijaDostavljaca> getByDostavljac(
            @PathVariable Long dostavljacId) {

        return repository.findByDostavljacId(dostavljacId);
    }
}