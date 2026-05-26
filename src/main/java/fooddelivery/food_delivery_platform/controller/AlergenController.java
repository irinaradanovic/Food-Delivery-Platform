package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.model.Alergen;
import fooddelivery.food_delivery_platform.repository.AlergenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alergeni")
@CrossOrigin(origins = "*")
public class AlergenController {

    @Autowired
    private AlergenRepository alergenRepository;

    @GetMapping
    public List<Alergen> getAll() {
        return alergenRepository.findAll();
    }
}
