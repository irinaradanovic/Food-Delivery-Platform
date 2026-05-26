package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.DostavljacRepository;
import fooddelivery.food_delivery_platform.repository.LokacijaDostavljacaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DostavljacService {

    private final DostavljacRepository dostavljacRepository;
    private final LokacijaDostavljacaRepository lokacijaRepository;

    public DostavljacService(DostavljacRepository dostavljacRepository,
                             LokacijaDostavljacaRepository lokacijaRepository) {
        this.dostavljacRepository = dostavljacRepository;
        this.lokacijaRepository = lokacijaRepository;
    }

    public List<Dostavljac> getAll() {
        return dostavljacRepository.findAll();
    }

    public Dostavljac save(Dostavljac dostavljac) {
        return dostavljacRepository.save(dostavljac);
    }

    public Dostavljac updateLocation(Long id, Double lat, Double lng) {

        Dostavljac dostavljac = dostavljacRepository.findById(id).orElseThrow();

        dostavljac.setTrenutnaLat(lat);
        dostavljac.setTrenutnaLng(lng);

        LokacijaDostavljaca lokacija = new LokacijaDostavljaca();
        lokacija.setDostavljac(dostavljac);
        lokacija.setLatitude(lat);
        lokacija.setLongitude(lng);
        lokacija.setTimestamp(LocalDateTime.now());

        lokacijaRepository.save(lokacija);

        return dostavljacRepository.save(dostavljac);
    }

    public List<Dostavljac> dostupniDostavljaci() {
        return dostavljacRepository.findByStatus(StatusDostavljaca.DOSTUPAN);
    }
}