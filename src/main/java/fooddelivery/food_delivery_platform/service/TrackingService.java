package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final KlikRepository klikRepo;
    private final PretragaRepository pretragaRepo;
    private final KupacRepository kupacRepo;
    private final ProizvodRepository proizvodRepo;

    public Klik zabeleziKlik(Long kupacId, Long proizvodId, String tipAkcije) {
        Kupac kupac = kupacRepo.findById(kupacId)
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + kupacId));
        Proizvod proizvod = proizvodRepo.findById(proizvodId)
                .orElseThrow(() -> new RuntimeException("Proizvod nije pronađen: " + proizvodId));

        return klikRepo.save(Klik.builder()
                .kupac(kupac)
                .proizvod(proizvod)
                .vremeKlika(LocalDateTime.now())
                .tipAkcije(tipAkcije)
                .build());
    }

    public Pretraga zabeleziPretragu(Long kupacId, String tekstUpita, String tipPretrage) {
        Kupac kupac = kupacRepo.findById(kupacId)
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + kupacId));

        return pretragaRepo.save(Pretraga.builder()
                .kupac(kupac)
                .tekstUpita(tekstUpita)
                .vremePretrage(LocalDateTime.now())
                .tipPretrage(tipPretrage)
                .build());
    }

    public List<Klik> getKlikovi(Long kupacId) {
        return klikRepo.findByKupac_KorisnikIdOrderByVremeKlikaDesc(kupacId);
    }

    public List<Pretraga> getPretrage(Long kupacId) {
        return pretragaRepo.findByKupac_KorisnikIdOrderByVremePretrageDesc(kupacId);
    }
}