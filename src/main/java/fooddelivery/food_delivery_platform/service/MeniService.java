package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeniService {

    @Autowired
    private MeniRepository meniRepository;

    public List<Meni> getAll() { return meniRepository.findAll(); }

    public List<Meni> findByRestoranRestoranId(Long restoranId) {
        return meniRepository.findByRestoranRestoranId(restoranId);
    }
}
