package fooddelivery.food_delivery_platform.controller;

import fooddelivery.food_delivery_platform.dto.CheckoutPreviewDTO;
import fooddelivery.food_delivery_platform.dto.CheckoutStavkaDTO;
import fooddelivery.food_delivery_platform.dto.KreiranjePorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.PorudzbinaPregledDTO;
import fooddelivery.food_delivery_platform.dto.PromenaStatusaPorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.PromenaStatusaPorudzbineResponseDTO;
import fooddelivery.food_delivery_platform.dto.StatusPorudzbineIstorijaDTO;
import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.model.StatusPorudzbineIstorija;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.model.StavkaPorudzbine;
import fooddelivery.food_delivery_platform.model.TipRacuna;
import fooddelivery.food_delivery_platform.service.PorudzbinaService;
import fooddelivery.food_delivery_platform.service.RacunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/porudzbine")
public class PorudzbinaController {

    private final PorudzbinaService porudzbinaService;
    private final RacunService racunService;

    @GetMapping
    public ResponseEntity<List<PorudzbinaPregledDTO>> getAll(
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @RequestParam(required = false) Long kupacId
    ) {
        return ResponseEntity.ok(porudzbinaService.getAll(trenutniKorisnikId, kupacId).stream()
                .map(this::toPregledDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId
    ) {
        try {
            return ResponseEntity.ok(toPregledDTO(porudzbinaService.getById(trenutniKorisnikId, id)));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("greska", e.getMessage()));
        }
    }

    @GetMapping("/{id}/istorija-statusa")
        public ResponseEntity<?> getStatusHistory(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId
        ) {
        try {
            List<StatusPorudzbineIstorija> istorija = porudzbinaService.getStatusHistory(trenutniKorisnikId, id);
            List<StatusPorudzbineIstorijaDTO> response = istorija.stream()
                    .map(item -> StatusPorudzbineIstorijaDTO.builder()
                            .statusIstorijaId(item.getStatusIstorijaId())
                            .porudzbinaId(item.getPorudzbina().getPorudzbinaId())
                            .status(item.getStatus())
                            .vremePromene(item.getVremePromene())
                            .promenioKorisnikId(item.getPromenioKorisnikId())
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("greska", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @Valid @RequestBody KreiranjePorudzbineDTO dto
    ) {
        try {
            Porudzbina nova = porudzbinaService.create(trenutniKorisnikId, dto);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("poruka", "Porudzbina kreirana");
            response.put("porudzbinaId", nova.getPorudzbinaId());
            response.put("status", nova.getStatus());
            response.put("ukupnaCena", nova.getUkupnaCena());
            response.put("statusPlacanja", nova.getStatusPlacanja());
            response.put("dostavljacId", nova.getDostavljacId());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<?> preview(
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @Valid @RequestBody CheckoutPreviewDTO dto
    ) {
        try {
            return ResponseEntity.ok(porudzbinaService.preview(trenutniKorisnikId, dto));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> promeniStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId,
            @Valid @RequestBody PromenaStatusaPorudzbineDTO dto
    ) {
        try {
            Porudzbina azurirana = porudzbinaService.promeniStatus(trenutniKorisnikId, id, dto);
            PromenaStatusaPorudzbineResponseDTO response = PromenaStatusaPorudzbineResponseDTO.builder()
                .porudzbinaId(azurirana.getPorudzbinaId())
                .status(azurirana.getStatus())
                .vremePromene(azurirana.getIstorijaStatusa().isEmpty()
                    ? null
                    : azurirana.getIstorijaStatusa().get(azurirana.getIstorijaStatusa().size() - 1).getVremePromene())
                .promenioKorisnikId(trenutniKorisnikId)
                .build();
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}/racun", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> preuzmiRacun(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId
    ) {
        return preuzmiPdf(id, trenutniKorisnikId, TipRacuna.RACUN, "racun-" + id + ".pdf");
    }

    @GetMapping(value = "/{id}/storno-racun", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> preuzmiStornoRacun(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long trenutniKorisnikId
    ) {
        return preuzmiPdf(id, trenutniKorisnikId, TipRacuna.STORNO, "storno-racun-" + id + ".pdf");
    }

    private ResponseEntity<?> preuzmiPdf(Long id, Long trenutniKorisnikId, TipRacuna tip, String filename) {
        try {
            porudzbinaService.getById(trenutniKorisnikId, id);
            byte[] pdf = racunService.generisiPdf(id, tip);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("greska", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("greska", e.getMessage()));
        }
    }

    private PorudzbinaPregledDTO toPregledDTO(Porudzbina porudzbina) {
        return PorudzbinaPregledDTO.builder()
                .porudzbinaId(porudzbina.getPorudzbinaId())
                .kupacId(porudzbina.getKupac() != null ? porudzbina.getKupac().getKorisnikId() : null)
                .adresaDostave(porudzbina.getAdresaDostave())
                .datumKreiranja(porudzbina.getDatumKreiranja())
                .napomena(porudzbina.getNapomena())
                .status(porudzbina.getStatus())
                .nacinPlacanja(porudzbina.getNacinPlacanja())
                .statusPlacanja(porudzbina.getStatusPlacanja())
                .cenaArtikala(porudzbina.getCenaArtikala())
                .cenaDostave(porudzbina.getCenaDostave())
                .popustArtikli(porudzbina.getPopustArtikli())
                .popustDostava(porudzbina.getPopustDostava())
                .iznosKarticom(porudzbina.getIznosKarticom())
                .iznosKes(porudzbina.getIznosKes())
                .ukupnaCena(porudzbina.getUkupnaCena())
                .kuponId(porudzbina.getKupon() != null ? porudzbina.getKupon().getKuponId() : null)
                .kuponKod(porudzbina.getKupon() != null ? porudzbina.getKupon().getKod() : null)
                .dostavljacId(porudzbina.getDostavljacId())
                .imaRacun(racunService.postojiRacun(porudzbina.getPorudzbinaId()))
                .imaStorno(racunService.postojiStorno(porudzbina.getPorudzbinaId()))
                .stavke(porudzbina.getStavke().stream().map(this::toStavkaDTO).collect(Collectors.toList()))
                .build();
    }

    private CheckoutStavkaDTO toStavkaDTO(StavkaPorudzbine stavka) {
        StavkaMenija stavkaMenija = stavka.getStavkaMenija();
        Proizvod proizvod = stavkaMenija != null ? stavkaMenija.getProizvod() : null;
        return CheckoutStavkaDTO.builder()
                .stavkaMenijaId(stavkaMenija != null ? stavkaMenija.getStavkaId() : null)
                .proizvodId(proizvod != null ? proizvod.getProizvodId() : null)
                .naziv(proizvod != null ? proizvod.getNaziv() : "Stavka menija")
                .kolicina(stavka.getKolicina())
                .jedinicnaCena(stavkaMenija != null ? stavkaMenija.getCena() : null)
                .ukupnaCena(stavka.getCena())
                .napomena(stavka.getNapomena())
                .build();
    }
}

