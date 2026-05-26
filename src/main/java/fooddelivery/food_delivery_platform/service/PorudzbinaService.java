package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.KreiranjePorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.PromenaStatusaPorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.StavkaPorudzbineDTO;
import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.KupacRepository;
import fooddelivery.food_delivery_platform.repository.PorudzbinaRepository;
import fooddelivery.food_delivery_platform.repository.KuponRepository;
import fooddelivery.food_delivery_platform.repository.StatusPorudzbineIstorijaRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PorudzbinaService {

    private final PorudzbinaRepository porudzbinaRepository;
    private final KupacRepository kupacRepository;
    private final StavkaMenijaRepository stavkaMenijaRepository;
    private final KuponRepository kuponRepository;
    private final StatusPorudzbineIstorijaRepository statusPorudzbineIstorijaRepository;

    private static final Map<StatusPorudzbine, Set<StatusPorudzbine>> DOZVOLJENI_PRELASCI = Map.of(
            StatusPorudzbine.KREIRANA, Set.of(StatusPorudzbine.PRIHVACENA, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.PRIHVACENA, Set.of(StatusPorudzbine.U_PRIPREMI, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.U_PRIPREMI, Set.of(StatusPorudzbine.KOD_DOSTAVLJACA, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.KOD_DOSTAVLJACA, Set.of(StatusPorudzbine.DOSTAVLJENA),
            StatusPorudzbine.DOSTAVLJENA, Set.of(),
            StatusPorudzbine.OTKAZANA, Set.of()
    );

    @Transactional(readOnly = true)
    public List<Porudzbina> getAll() {
        return porudzbinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Porudzbina getById(Long id) {
        return porudzbinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Porudžbina nije pronađena: " + id));
    }

    @Transactional(readOnly = true)
    public List<Porudzbina> getByKupac(Long kupacId) {
        return porudzbinaRepository.findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(kupacId);
    }

    @Transactional(readOnly = true)
    public List<StatusPorudzbineIstorija> getStatusHistory(Long porudzbinaId) {
        getById(porudzbinaId);
        return statusPorudzbineIstorijaRepository.findByPorudzbinaPorudzbinaIdOrderByVremePromeneAsc(porudzbinaId);
    }

    @Transactional
    public Porudzbina create(KreiranjePorudzbineDTO dto) {
        Kupac kupac = kupacRepository.findById(dto.getKupacId())
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + dto.getKupacId()));


        Porudzbina porudzbina = Porudzbina.builder()
                .kupac(kupac)
                .adresaDostave(dto.getAdresaDostave())
                .napomena(dto.getNapomena())
                .datumKreiranja(LocalDateTime.now())
                .status(StatusPorudzbine.KREIRANA)
                .ukupnaCena(BigDecimal.ZERO)
                .build();

        BigDecimal ukupnaCena = BigDecimal.ZERO;

        for (StavkaPorudzbineDTO stavkaDTO : dto.getStavke()) {
            StavkaMenija stavkaMenija = stavkaMenijaRepository.findById(stavkaDTO.getStavkaMenijaId())
                    .orElseThrow(() -> new RuntimeException("Stavka menija nije pronađena: " + stavkaDTO.getStavkaMenijaId()));

            if (!stavkaMenija.isDostupno() || stavkaMenija.isObrisan()) {
                throw new RuntimeException("Stavka menija nije dostupna: " + stavkaDTO.getStavkaMenijaId());
            }

            BigDecimal cenaStavke = stavkaMenija.getCena()
                    .multiply(BigDecimal.valueOf(stavkaDTO.getKolicina()))
                    .setScale(2, RoundingMode.HALF_UP);

            StavkaPorudzbine stavka = StavkaPorudzbine.builder()
                    .porudzbina(porudzbina)
                    .stavkaMenija(stavkaMenija)
                    .kolicina(stavkaDTO.getKolicina())
                    .cena(cenaStavke)
                    .build();

            porudzbina.getStavke().add(stavka);
            ukupnaCena = ukupnaCena.add(cenaStavke);
        }

        porudzbina.setUkupnaCena(ukupnaCena.setScale(2, RoundingMode.HALF_UP));

        if (dto.getKuponKod() != null && !dto.getKuponKod().isBlank()) {
            kuponRepository.findByKod(dto.getKuponKod()).ifPresent(kupon -> {
                boolean valid = kupon.isAktivan();
                if (kupon.getVaziOd() != null && kupon.getVaziOd().isAfter(LocalDateTime.now())) valid = false;
                if (kupon.getVaziDo() != null && kupon.getVaziDo().isBefore(LocalDateTime.now())) valid = false;
                if (kupon.getMaxUpotreba() != null && kupon.getUpotrebljenoPuta() != null && kupon.getUpotrebljenoPuta() >= kupon.getMaxUpotreba()) valid = false;
                if (!valid) throw new RuntimeException("Kupon nije validan ili više ne važi: " + dto.getKuponKod());

                porudzbina.setKupon(kupon);
                kupon.setUpotrebljenoPuta((kupon.getUpotrebljenoPuta() == null ? 0 : kupon.getUpotrebljenoPuta()) + 1);
                if (kupon.getMaxUpotreba() != null && kupon.getUpotrebljenoPuta() >= kupon.getMaxUpotreba()) kupon.setAktivan(false);
                kuponRepository.save(kupon);
            });
        }

        StatusPorudzbineIstorija prviZapis = StatusPorudzbineIstorija.builder()
                .porudzbina(porudzbina)
                .status(StatusPorudzbine.KREIRANA)
                .vremePromene(LocalDateTime.now())
                .promenioKorisnikId(null)
                .build();

        porudzbina.getIstorijaStatusa().add(prviZapis);

        return porudzbinaRepository.save(porudzbina);
    }

    @Transactional
    public Porudzbina promeniStatus(Long porudzbinaId, PromenaStatusaPorudzbineDTO dto) {
        Porudzbina porudzbina = getById(porudzbinaId);

        StatusPorudzbine trenutniStatus = porudzbina.getStatus();
        StatusPorudzbine noviStatus = dto.getNoviStatus();

        if (trenutniStatus == noviStatus) {
            throw new RuntimeException("Porudžbina je već u statusu: " + noviStatus);
        }

        Set<StatusPorudzbine> dozvoljeniPrelazi = DOZVOLJENI_PRELASCI.getOrDefault(trenutniStatus, Set.of());
        if (!dozvoljeniPrelazi.contains(noviStatus)) {
            throw new RuntimeException("Nedozvoljen prelaz statusa iz " + trenutniStatus + " u " + noviStatus);
        }

        porudzbina.setStatus(noviStatus);

        StatusPorudzbineIstorija zapis = StatusPorudzbineIstorija.builder()
                .porudzbina(porudzbina)
                .status(noviStatus)
                .vremePromene(LocalDateTime.now())
                .promenioKorisnikId(dto.getPromenioKorisnikId())
                .build();

        porudzbina.getIstorijaStatusa().add(zapis);

        return porudzbinaRepository.save(porudzbina);
    }
}