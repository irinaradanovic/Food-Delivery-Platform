package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.DostavaRepository;
import fooddelivery.food_delivery_platform.repository.DostavljacRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DostavaService {

    private final DostavaRepository dostavaRepository;
    private final DostavljacRepository dostavljacRepository;

    public DostavaService(DostavaRepository dostavaRepository,
                          DostavljacRepository dostavljacRepository) {
        this.dostavaRepository = dostavaRepository;
        this.dostavljacRepository = dostavljacRepository;
    }

    public Dostava kreirajDostavu(Dostava dostava) {

        dostava.setVremeKreiranja(LocalDateTime.now());
        dostava.setStatus(StatusDostave.KREIRANA);

        return dostavaRepository.save(dostava);
    }

    public Dostava dodeliDostavljaca(Long dostavaId, Long dostavljacId) {

        Dostava dostava = dostavaRepository.findById(dostavaId).orElseThrow();

        Dostavljac dostavljac = dostavljacRepository.findById(dostavljacId).orElseThrow();

        dostava.setDostavljac(dostavljac);
        dostava.setStatus(StatusDostave.DODELJENA);

        dostavljac.setStatus(StatusDostavljaca.ZAUZET);

        dostavljacRepository.save(dostavljac);

        return dostavaRepository.save(dostava);
    }

    public Dostava promeniStatus(Long dostavaId, StatusDostave status) {

        Dostava dostava = dostavaRepository.findById(dostavaId).orElseThrow();

        dostava.setStatus(status);

        if(status == StatusDostave.ISPORUCENA) {

            dostava.setVremeIsporuke(LocalDateTime.now());

            Dostavljac dostavljac = dostava.getDostavljac();

            dostavljac.setStatus(StatusDostavljaca.DOSTUPAN);

            dostavljac.setBrojDostava(
                    dostavljac.getBrojDostava() + 1
            );

            dostavljacRepository.save(dostavljac);
        }

        return dostavaRepository.save(dostava);
    }

    public List<Dostava> sveDostave() {
        return dostavaRepository.findAll();
    }
}