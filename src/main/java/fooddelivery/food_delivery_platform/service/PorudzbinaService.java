package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.KreiranjePorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.PromenaStatusaPorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.StavkaPorudzbineDTO;
import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import fooddelivery.food_delivery_platform.repository.KupacRepository;
import fooddelivery.food_delivery_platform.repository.KlikRepository; // DODATO
import fooddelivery.food_delivery_platform.repository.PorudzbinaRepository;
import fooddelivery.food_delivery_platform.repository.KuponRepository;
import fooddelivery.food_delivery_platform.repository.StatusPorudzbineIstorijaRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PorudzbinaService {

    private final PorudzbinaRepository porudzbinaRepository;
    private final KupacRepository kupacRepository;
    private final KorisnikRepository korisnikRepository;
    private final StavkaMenijaRepository stavkaMenijaRepository;
    private final KuponRepository kuponRepository;
    private final StatusPorudzbineIstorijaRepository statusPorudzbineIstorijaRepository;
    private final KlikRepository klikRepository;

    private static final Map<StatusPorudzbine, Set<StatusPorudzbine>> DOZVOLJENI_PRELASCI = Map.of(
            StatusPorudzbine.KREIRANA, Set.of(StatusPorudzbine.PRIHVACENA, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.PRIHVACENA, Set.of(StatusPorudzbine.U_PRIPREMI, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.U_PRIPREMI, Set.of(StatusPorudzbine.KOD_DOSTAVLJACA, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.KOD_DOSTAVLJACA, Set.of(StatusPorudzbine.DOSTAVLJENA),
            StatusPorudzbine.DOSTAVLJENA, Set.of(),
            StatusPorudzbine.OTKAZANA, Set.of()
    );

    @Transactional(readOnly = true)
    public List<Porudzbina> getAll(Long trenutniKorisnikId, Long kupacId) {
        Korisnik korisnik = getKorisnik(trenutniKorisnikId);
        if (isKupac(korisnik)) {
            if (kupacId != null && !kupacId.equals(trenutniKorisnikId)) {
                throw new AccessDeniedException("Nemate ovlašćenje da pregledate porudžbine drugog kupca.");
            }
            List<Porudzbina> porudzbine = porudzbinaRepository.findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(trenutniKorisnikId);
            porudzbine.forEach(this::applyMockDostavljac);
            return porudzbine;
        }
        if (isMenadzer(korisnik)) {
            List<Porudzbina> porudzbine = porudzbinaRepository.findAll().stream()
                    .filter(p -> isMenadzerZaPorudzbinu(trenutniKorisnikId, p))
                    .toList();
            porudzbine.forEach(this::applyMockDostavljac);
            return porudzbine;
        }
        throw new AccessDeniedException("Nemate ovlašćenje za pregled porudžbina.");
    }

    @Transactional(readOnly = true)
    public Porudzbina getById(Long trenutniKorisnikId, Long id) {
        Porudzbina porudzbina = porudzbinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Porudžbina nije pronađena: " + id));
        authorizeAccess(trenutniKorisnikId, porudzbina);
        applyMockDostavljac(porudzbina);
        return porudzbina;
    }

    @Transactional(readOnly = true)
    public List<StatusPorudzbineIstorija> getStatusHistory(Long trenutniKorisnikId, Long porudzbinaId) {
        Porudzbina porudzbina = getById(trenutniKorisnikId, porudzbinaId);
        return statusPorudzbineIstorijaRepository.findByPorudzbinaPorudzbinaIdOrderByVremePromeneAsc(porudzbina.getPorudzbinaId());
    }

    @Transactional
    public Porudzbina create(Long trenutniKorisnikId, KreiranjePorudzbineDTO dto) {
        Korisnik korisnik = getKorisnik(trenutniKorisnikId);
        if (!isKupac(korisnik)) {
            throw new AccessDeniedException("Samo kupac može da kreira porudžbinu.");
        }
        if (!trenutniKorisnikId.equals(dto.getKupacId())) {
            throw new AccessDeniedException("Nemate ovlašćenje da kreirate porudžbinu za drugog kupca.");
        }
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

        applyMockDostavljac(porudzbina);

        BigDecimal ukupnaCena = BigDecimal.ZERO;


        List<Proizvod> kupljeniProizvodi = new ArrayList<>();

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

            if (stavkaMenija.getProizvod() != null) {
                kupljeniProizvodi.add(stavkaMenija.getProizvod());
            }
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

        Porudzbina sacuvana = porudzbinaRepository.save(porudzbina);


        LocalDateTime sada = LocalDateTime.now();
        for (Proizvod proizvod : kupljeniProizvodi) {
            klikRepository.save(Klik.builder()
                    .kupac(kupac)
                    .proizvod(proizvod)
                    .vremeKlika(sada)
                    .tipAkcije("KUPOVINA")
                    .build());
        }

        return sacuvana;
    }

    @Transactional
    public Porudzbina promeniStatus(Long trenutniKorisnikId, Long porudzbinaId, PromenaStatusaPorudzbineDTO dto) {
        Porudzbina porudzbina = porudzbinaRepository.findById(porudzbinaId)
                .orElseThrow(() -> new RuntimeException("Porudžbina nije pronađena: " + porudzbinaId));

        Korisnik korisnik = getKorisnik(trenutniKorisnikId);
        boolean dozvoljenMenadzer = isMenadzer(korisnik) && isMenadzerZaPorudzbinu(trenutniKorisnikId, porudzbina);
        boolean dozvoljenDostavljac = isDostavljac(korisnik)
                && ((porudzbina.getStatus() == StatusPorudzbine.U_PRIPREMI && dto.getNoviStatus() == StatusPorudzbine.KOD_DOSTAVLJACA)
                || (porudzbina.getStatus() == StatusPorudzbine.KOD_DOSTAVLJACA && dto.getNoviStatus() == StatusPorudzbine.DOSTAVLJENA));
        if (!dozvoljenMenadzer && !dozvoljenDostavljac) {
            throw new AccessDeniedException("Nemate ovlašćenje da menjate status ove porudžbine.");
        }

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
                .promenioKorisnikId(trenutniKorisnikId)
                .build();

        porudzbina.getIstorijaStatusa().add(zapis);

        return porudzbinaRepository.save(porudzbina);
    }

    private Korisnik getKorisnik(Long korisnikId) {
        return korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen: " + korisnikId));
    }

    private boolean isKupac(Korisnik korisnik) {
        return korisnik.getUloga() != null && korisnik.getUloga().equalsIgnoreCase("KUPAC");
    }

    private boolean isMenadzer(Korisnik korisnik) {
        return korisnik.getUloga() != null && korisnik.getUloga().equalsIgnoreCase("MENADZER");
    }

    private boolean isDostavljac(Korisnik korisnik) {
        return korisnik.getUloga() != null && korisnik.getUloga().equalsIgnoreCase("DOSTAVLJAC");
    }

    private void authorizeAccess(Long korisnikId, Porudzbina porudzbina) {
        Korisnik korisnik = getKorisnik(korisnikId);
        if (isKupac(korisnik)) {
            if (!porudzbina.getKupac().getKorisnikId().equals(korisnikId)) {
                throw new AccessDeniedException("Nemate ovlašćenje za pregled ove porudžbine.");
            }
            return;
        }
        if (isMenadzer(korisnik)) {
            if (!isMenadzerZaPorudzbinu(korisnikId, porudzbina)) {
                throw new AccessDeniedException("Nemate ovlašćenje za pregled ove porudžbine.");
            }
            return;
        }
        throw new AccessDeniedException("Nemate ovlašćenje za pregled ove porudžbine.");
    }

    private boolean isMenadzerZaPorudzbinu(Long menadzerId, Porudzbina porudzbina) {
        return porudzbina.getStavke().stream()
                .map(StavkaPorudzbine::getStavkaMenija)
                .map(StavkaMenija::getMeni)
                .map(Meni::getRestoran)
                .filter(restoran -> restoran != null && restoran.getMenadzer() != null)
                .anyMatch(restoran -> menadzerId.equals(restoran.getMenadzer().getKorisnikId()));
    }

    private void applyMockDostavljac(Porudzbina porudzbina) {
        porudzbina.setDostavljacId(6L);
    }
}