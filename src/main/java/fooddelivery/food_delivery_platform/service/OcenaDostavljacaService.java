package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Dostavljac;
import fooddelivery.food_delivery_platform.model.OcenaDostavljaca;
import fooddelivery.food_delivery_platform.repository.DostavljacRepository;
import fooddelivery.food_delivery_platform.repository.OcenaDostavljacaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OcenaDostavljacaService {

    private final OcenaDostavljacaRepository ocenaRepository;
    private final DostavljacRepository dostavljacRepository;

    public OcenaDostavljacaService(OcenaDostavljacaRepository ocenaRepository,
                                   DostavljacRepository dostavljacRepository) {
        this.ocenaRepository = ocenaRepository;
        this.dostavljacRepository = dostavljacRepository;
    }

    public OcenaDostavljaca dodajOcenu(OcenaDostavljaca ocena) {

        OcenaDostavljaca sacuvana = ocenaRepository.save(ocena);

        List<OcenaDostavljaca> ocene =
                ocenaRepository.findByDostavljacId(
                        ocena.getDostavljac().getId()
                );

        double prosek =
                ocene.stream()
                        .mapToInt(OcenaDostavljaca::getOcena)
                        .average()
                        .orElse(0);

        Dostavljac dostavljac =
                ocena.getDostavljac();

        dostavljac.setProsecnaOcena(prosek);

        dostavljacRepository.save(dostavljac);

        return sacuvana;
    }
}
