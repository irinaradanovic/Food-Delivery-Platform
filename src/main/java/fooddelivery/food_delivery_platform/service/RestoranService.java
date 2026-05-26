package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.repository.RestoranRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestoranService {

    @Autowired
    private RestoranRepository restoranRepo;

    public List<Restoran> findByMenadzerKorisnikId(Long menadzerId) {
        return restoranRepo.findByMenadzerKorisnikId(menadzerId);
    }


}
