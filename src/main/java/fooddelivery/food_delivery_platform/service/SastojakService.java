package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Sastojak;
import fooddelivery.food_delivery_platform.repository.SastojakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SastojakService {

    @Autowired
    private SastojakRepository sastojakRepository;

    public List<Sastojak> getIngredientsForManager(Long trenutniKorisnikId){
        return sastojakRepository.findByKreiraoKorisnikIdIsNullOrKreiraoKorisnikId(trenutniKorisnikId);
    }
}
