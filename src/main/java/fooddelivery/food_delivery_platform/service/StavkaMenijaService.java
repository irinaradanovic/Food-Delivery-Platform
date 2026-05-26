package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StavkaMenijaService {

    @Autowired
    private StavkaMenijaRepository stavkaMenijaRepository;

    @Transactional(readOnly = true)
    public List<StavkaMenija> getItemsByMenu(Long meniId) {
        return stavkaMenijaRepository.findByMeniMeniIdAndObrisanFalse(meniId);
    }

}