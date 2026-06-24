package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.KomboRequestDTO;
import fooddelivery.food_delivery_platform.dto.KomboResultDTO;
import fooddelivery.food_delivery_platform.model.Korisnik;
import fooddelivery.food_delivery_platform.model.PrikazaniKombo;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import fooddelivery.food_delivery_platform.repository.PrikazaniKomboRepository;
import fooddelivery.food_delivery_platform.service.KomboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kombo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KomboController {

    private final KomboService komboService;
    private final PrikazaniKomboRepository prikazaniKomboRepository;
    private final KorisnikRepository korisnikRepository;

    @PostMapping("/predlozi")
    public ResponseEntity<List<KomboResultDTO>> predloziKomboe(
            @RequestBody KomboRequestDTO request,
            @RequestParam(required = false) Long kupacId) {

        List<KomboResultDTO> komboi = komboService.predloziKomboe(request);

        if (kupacId != null && !komboi.isEmpty()) {
            snimiPrikazaneKomboe(kupacId, komboi);
        }

        return ResponseEntity.ok(komboi);
    }

    private void snimiPrikazaneKomboe(Long kupacId, List<KomboResultDTO> komboi) {
        Korisnik kupac = korisnikRepository.findById(kupacId).orElse(null);
        if (kupac == null) return;

        LocalDateTime sada = LocalDateTime.now();
        final Korisnik finalKupac = kupac;

        List<PrikazaniKombo> zapisi = komboi.stream()
                .map(k -> {
                    List<Long> stavkeIds = k.getStavke().stream()
                            .map(KomboResultDTO.KomboStavkaDTO::getStavkaId)
                            .collect(Collectors.toList());
                    return PrikazaniKombo.builder()
                            .kupac(finalKupac)
                            .stavkeMenijaIds(stavkeIds)
                            .prikazanoU(sada)
                            .uspesna(false)
                            .brojNarucenihStavki(0)
                            .build();
                })
                .collect(Collectors.toList());

        prikazaniKomboRepository.saveAll(zapisi);
    }
}