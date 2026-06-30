package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.CheckoutPreviewDTO;
import fooddelivery.food_delivery_platform.dto.CheckoutStavkaDTO;
import fooddelivery.food_delivery_platform.dto.CheckoutSummaryDTO;
import fooddelivery.food_delivery_platform.dto.KreiranjePorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.PromenaStatusaPorudzbineDTO;
import fooddelivery.food_delivery_platform.dto.StavkaPorudzbineDTO;
import fooddelivery.food_delivery_platform.model.Klik;
import fooddelivery.food_delivery_platform.model.Korisnik;
import fooddelivery.food_delivery_platform.model.Kupon;
import fooddelivery.food_delivery_platform.model.Kupac;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.NacinPlacanja;
import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.PrikazaniKombo;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.model.Dostava;
import fooddelivery.food_delivery_platform.model.Dostavljac;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.model.StatusDostave;
import fooddelivery.food_delivery_platform.model.StatusDostavljaca;
import fooddelivery.food_delivery_platform.model.StatusPlacanja;
import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import fooddelivery.food_delivery_platform.model.StatusPorudzbineIstorija;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.model.StavkaPorudzbine;
import fooddelivery.food_delivery_platform.repository.DostavaRepository;
import fooddelivery.food_delivery_platform.repository.DostavljacRepository;
import fooddelivery.food_delivery_platform.repository.KlikRepository;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import fooddelivery.food_delivery_platform.repository.KupacRepository;
import fooddelivery.food_delivery_platform.repository.KuponRepository;
import fooddelivery.food_delivery_platform.repository.PorudzbinaRepository;
import fooddelivery.food_delivery_platform.repository.PrikazanaPreporukaRepository;
import fooddelivery.food_delivery_platform.repository.PrikazaniKomboRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import fooddelivery.food_delivery_platform.repository.StatusPorudzbineIstorijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PorudzbinaService {

    private static final BigDecimal CENA_DOSTAVE = new BigDecimal("250.00");
    private static final BigDecimal MIN_KES_KOMBINOVANO = new BigDecimal("100.00");

    private final PorudzbinaRepository porudzbinaRepository;
    private final KupacRepository kupacRepository;
    private final KorisnikRepository korisnikRepository;
    private final StavkaMenijaRepository stavkaMenijaRepository;
    private final KuponRepository kuponRepository;
    private final StatusPorudzbineIstorijaRepository statusPorudzbineIstorijaRepository;
    private final KlikRepository klikRepository;
    private final DostavaRepository dostavaRepository;
    private final DostavljacRepository dostavljacRepository;
    private final RacunService racunService;
    private final PrikazanaPreporukaRepository prikazanaPreporukaRepository;
    private final PrikazaniKomboRepository prikazaniKomboRepository;

    private static final Map<StatusPorudzbine, Set<StatusPorudzbine>> DOZVOLJENI_PRELASCI = Map.of(
            StatusPorudzbine.KREIRANA, Set.of(StatusPorudzbine.POTVRDJENA, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.POTVRDJENA, Set.of(StatusPorudzbine.U_PRIPREMI, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.U_PRIPREMI, Set.of(StatusPorudzbine.SPREMNA_ZA_DOSTAVU, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.SPREMNA_ZA_DOSTAVU, Set.of(StatusPorudzbine.PREUZETA, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.PREUZETA, Set.of(StatusPorudzbine.ISPORUCENA, StatusPorudzbine.OTKAZANA),
            StatusPorudzbine.ISPORUCENA, Set.of(),
            StatusPorudzbine.OTKAZANA, Set.of()
    );

    @Transactional(readOnly = true)
    public List<Porudzbina> getAll(Long trenutniKorisnikId, Long kupacId) {
        Korisnik korisnik = getKorisnik(trenutniKorisnikId);
        if (isKupac(korisnik)) {
            if (kupacId != null && !kupacId.equals(trenutniKorisnikId)) {
                throw new AccessDeniedException("Nemate ovlascenje da pregledate porudzbine drugog kupca.");
            }
            List<Porudzbina> porudzbine = porudzbinaRepository.findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(trenutniKorisnikId);
            porudzbine.forEach(this::applyDostavljacIzDostave);
            return porudzbine;
        }
        if (isMenadzer(korisnik)) {
            List<Porudzbina> porudzbine = porudzbinaRepository.findAll().stream()
                    .filter(p -> isMenadzerZaPorudzbinu(trenutniKorisnikId, p))
                    .toList();
            porudzbine.forEach(this::applyDostavljacIzDostave);
            return porudzbine;
        }
        if (isDostavljac(korisnik)) {
            List<Long> porudzbinaIds = dostavaRepository.findByDostavljacId(trenutniKorisnikId).stream()
                    .map(Dostava::getPorudzbinaId)
                    .filter(id -> id != null)
                    .toList();
            if (porudzbinaIds.isEmpty()) {
                return List.of();
            }
            List<Porudzbina> porudzbine = porudzbinaRepository.findByPorudzbinaIdInOrderByDatumKreiranjaDesc(porudzbinaIds);
            porudzbine.forEach(this::applyDostavljacIzDostave);
            return porudzbine;
        }
        throw new AccessDeniedException("Nemate ovlascenje za pregled porudzbina.");
    }

    @Transactional(readOnly = true)
    public Porudzbina getById(Long trenutniKorisnikId, Long id) {
        Porudzbina porudzbina = porudzbinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Porudzbina nije pronadjena: " + id));
        authorizeAccess(trenutniKorisnikId, porudzbina);
        applyDostavljacIzDostave(porudzbina);
        return porudzbina;
    }

    @Transactional(readOnly = true)
    public List<StatusPorudzbineIstorija> getStatusHistory(Long trenutniKorisnikId, Long porudzbinaId) {
        Porudzbina porudzbina = getById(trenutniKorisnikId, porudzbinaId);
        return statusPorudzbineIstorijaRepository.findByPorudzbinaPorudzbinaIdOrderByVremePromeneAsc(porudzbina.getPorudzbinaId());
    }

    @Transactional(readOnly = true)
    public CheckoutSummaryDTO preview(Long trenutniKorisnikId, CheckoutPreviewDTO dto) {
        Korisnik korisnik = getKorisnik(trenutniKorisnikId);
        if (!isKupac(korisnik) || !trenutniKorisnikId.equals(dto.getKupacId())) {
            throw new AccessDeniedException("Nemate ovlascenje za obracun ove porudzbine.");
        }
        Obracun obracun = obracunaj(dto.getKupacId(), dto.getStavke(), dto.getKuponId(), dto.getKuponKod(), dto.getNacinPlacanja(), dto.getIznosKarticom());
        return obracun.summary();
    }

    @Transactional
    public Porudzbina create(Long trenutniKorisnikId, KreiranjePorudzbineDTO dto) {
        Korisnik korisnik = getKorisnik(trenutniKorisnikId);
        if (!isKupac(korisnik)) {
            throw new AccessDeniedException("Samo kupac moze da kreira porudzbinu.");
        }
        if (!trenutniKorisnikId.equals(dto.getKupacId())) {
            throw new AccessDeniedException("Nemate ovlascenje da kreirate porudzbinu za drugog kupca.");
        }
        Kupac kupac = kupacRepository.findById(dto.getKupacId())
                .orElseThrow(() -> new RuntimeException("Kupac nije pronadjen: " + dto.getKupacId()));

        Obracun obracun = obracunaj(dto.getKupacId(), dto.getStavke(), dto.getKuponId(), dto.getKuponKod(), dto.getNacinPlacanja(), dto.getIznosKarticom());

        Porudzbina porudzbina = Porudzbina.builder()
                .kupac(kupac)
                .adresaDostave(dto.getAdresaDostave())
                .napomena(dto.getNapomena())
                .datumKreiranja(LocalDateTime.now())
                .status(StatusPorudzbine.KREIRANA)
                .nacinPlacanja(dto.getNacinPlacanja())
                .statusPlacanja(pocetniStatusPlacanja(dto.getNacinPlacanja()))
                .cenaArtikala(obracun.cenaArtikala())
                .cenaDostave(obracun.cenaDostave())
                .popustArtikli(obracun.popustArtikli())
                .popustDostava(obracun.popustDostava())
                .iznosKarticom(obracun.iznosKarticom())
                .iznosKes(obracun.iznosKes())
                .ukupnaCena(obracun.ukupnaCena())
                .kupon(obracun.kupon())
                .build();

        List<Proizvod> kupljeniProizvodi = new ArrayList<>();
        Set<Long> naruceneStavkeMenijaIds = new HashSet<>();
        for (ObracunStavka obracunStavka : obracun.stavke()) {
            StavkaPorudzbine stavka = StavkaPorudzbine.builder()
                    .porudzbina(porudzbina)
                    .stavkaMenija(obracunStavka.stavkaMenija())
                    .kolicina(obracunStavka.kolicina())
                    .cena(obracunStavka.ukupnaCena())
                    .napomena(obracunStavka.napomena())
                    .build();
            porudzbina.getStavke().add(stavka);
            naruceneStavkeMenijaIds.add(obracunStavka.stavkaMenija().getStavkaId());

            if (obracunStavka.stavkaMenija().getProizvod() != null) {
                kupljeniProizvodi.add(obracunStavka.stavkaMenija().getProizvod());
            }
        }

        porudzbina.getIstorijaStatusa().add(StatusPorudzbineIstorija.builder()
                .porudzbina(porudzbina)
                .status(StatusPorudzbine.KREIRANA)
                .vremePromene(LocalDateTime.now())
                .promenioKorisnikId(null)
                .build());

        Porudzbina sacuvana = porudzbinaRepository.save(porudzbina);
        zabeleziKupovinu(kupac, kupljeniProizvodi);
        oznaciPreporukeKaoUspesne(kupac.getKorisnikId(), kupljeniProizvodi);
        oznaciKomboeKaoUspesne(kupac.getKorisnikId(), naruceneStavkeMenijaIds);
        return sacuvana;
    }

    @Transactional
    public Porudzbina promeniStatus(Long trenutniKorisnikId, Long porudzbinaId, PromenaStatusaPorudzbineDTO dto) {
        Porudzbina porudzbina = porudzbinaRepository.findById(porudzbinaId)
                .orElseThrow(() -> new RuntimeException("Porudzbina nije pronadjena: " + porudzbinaId));

        Korisnik korisnik = getKorisnik(trenutniKorisnikId);
        StatusPorudzbine trenutniStatus = porudzbina.getStatus();
        StatusPorudzbine noviStatus = dto.getNoviStatus();

        if (trenutniStatus == noviStatus) {
            throw new RuntimeException("Porudzbina je vec u statusu: " + noviStatus);
        }
        if (!DOZVOLJENI_PRELASCI.getOrDefault(trenutniStatus, Set.of()).contains(noviStatus)) {
            throw new RuntimeException("Nedozvoljen prelaz statusa iz " + trenutniStatus + " u " + noviStatus);
        }
        if (!mozeDaMenjaStatus(korisnik, trenutniKorisnikId, porudzbina, noviStatus)) {
            throw new AccessDeniedException("Nemate ovlascenje da menjate status ove porudzbine.");
        }
        porudzbina.setStatus(noviStatus);
        azurirajPlacanjeZaStatus(porudzbina, noviStatus);
        oznaciKuponAkoJePlacanjeUspesno(porudzbina, noviStatus);
        sinhronizujDostavuZaStatus(porudzbina, noviStatus);
        dodajIstorijuStatusa(porudzbina, noviStatus, trenutniKorisnikId);
        Porudzbina sacuvana = porudzbinaRepository.save(porudzbina);
        if (noviStatus == StatusPorudzbine.POTVRDJENA) {
            racunService.izdajRacunAkoNePostoji(sacuvana, trenutniKorisnikId);
        } else if (noviStatus == StatusPorudzbine.OTKAZANA) {
            racunService.stornirajAkoPostoji(sacuvana, trenutniKorisnikId);
        }
        applyDostavljacIzDostave(sacuvana);
        return sacuvana;
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void automatskiOtkaziNepotvrdjene() {
        LocalDateTime granica = LocalDateTime.now().minusMinutes(15);
        List<Porudzbina> zastarele = porudzbinaRepository.findByStatusAndDatumKreiranjaBefore(StatusPorudzbine.KREIRANA, granica);
        for (Porudzbina porudzbina : zastarele) {
            porudzbina.setStatus(StatusPorudzbine.OTKAZANA);
            porudzbina.setStatusPlacanja(StatusPlacanja.OTKAZANO);
            dodajIstorijuStatusa(porudzbina, StatusPorudzbine.OTKAZANA, null);
        }
    }


    /*
      Označava sve nerealizovane preporuke kupca kao uspešne
      za one proizvode koji su se našli u ovoj narudžbini.
     */
    private void oznaciPreporukeKaoUspesne(Long kupacId, List<Proizvod> kupljeniProizvodi) {
        if (kupljeniProizvodi == null || kupljeniProizvodi.isEmpty()) return;
        List<Long> proizvodiIds = kupljeniProizvodi.stream()
                .map(Proizvod::getProizvodId)
                .distinct()
                .collect(Collectors.toList());
        prikazanaPreporukaRepository.oznaciKaoUspesne(kupacId, proizvodiIds, LocalDateTime.now());
    }

    /*
      Označava kombo kao uspešan ako je kupac naručio bar jednu stavku iz njega.
      Beleži i koliko stavki je tačno naručeno (brojNarucenihStavki).
     */
    private void oznaciKomboeKaoUspesne(Long kupacId, Set<Long> naruceneStavkeMenijaIds) {
        if (naruceneStavkeMenijaIds == null || naruceneStavkeMenijaIds.isEmpty()) return;

        List<PrikazaniKombo> kandidati = prikazaniKomboRepository
                .findByKupac_KorisnikIdAndUspesnaFalseOrderByPrikazanoUDesc(kupacId);

        LocalDateTime sada = LocalDateTime.now();

        for (PrikazaniKombo kombo : kandidati) {
            long poklapanja = kombo.getStavkeMenijaIds().stream()
                    .filter(naruceneStavkeMenijaIds::contains)
                    .count();
            if (poklapanja > 0) {
                kombo.setUspesna(true);
                kombo.setBrojNarucenihStavki((int) poklapanja);
                kombo.setRealizovanoU(sada);
            }
        }

        prikazaniKomboRepository.saveAll(kandidati);
    }

    private Obracun obracunaj(Long kupacId, List<StavkaPorudzbineDTO> stavkeDto, Long kuponId, String kuponKod,
                             NacinPlacanja nacinPlacanja, BigDecimal trazeniIznosKarticom) {
        if (nacinPlacanja == null) {
            throw new RuntimeException("Nacin placanja je obavezan.");
        }

        List<ObracunStavka> stavke = new ArrayList<>();
        BigDecimal cenaArtikala = BigDecimal.ZERO;

        for (StavkaPorudzbineDTO stavkaDTO : stavkeDto) {
            StavkaMenija stavkaMenija = stavkaMenijaRepository.findById(stavkaDTO.getStavkaMenijaId())
                    .orElseThrow(() -> new RuntimeException("Stavka menija nije pronadjena: " + stavkaDTO.getStavkaMenijaId()));
            if (!stavkaMenija.isDostupno() || stavkaMenija.isObrisan() || stavkaMenija.getMeni() == null || !stavkaMenija.getMeni().isAktivan()) {
                throw new RuntimeException("Stavka menija nije dostupna: " + stavkaDTO.getStavkaMenijaId());
            }

            BigDecimal jedinicnaCena = scale(stavkaMenija.getCena());
            BigDecimal ukupnaCena = scale(jedinicnaCena.multiply(BigDecimal.valueOf(stavkaDTO.getKolicina())));
            stavke.add(new ObracunStavka(stavkaMenija, stavkaDTO.getKolicina(), jedinicnaCena, ukupnaCena, stavkaDTO.getNapomena()));
            cenaArtikala = cenaArtikala.add(ukupnaCena);
        }

        cenaArtikala = scale(cenaArtikala);
        BigDecimal cenaDostave = CENA_DOSTAVE;
        BigDecimal popustArtikli = BigDecimal.ZERO;
        BigDecimal popustDostava = BigDecimal.ZERO;
        Kupon kupon = null;
        if (kuponId != null || (kuponKod != null && !kuponKod.isBlank())) {
            kupon = validirajKupon(kuponId, kuponKod, kupacId);
            BigDecimal ukupniPopust = izracunajPopust(kupon, cenaArtikala.add(cenaDostave));
            popustArtikli = ukupniPopust.min(cenaArtikala);
            popustDostava = scale(ukupniPopust.subtract(popustArtikli));
        }

        BigDecimal ukupnaCena = scale(cenaArtikala.subtract(popustArtikli).add(cenaDostave).subtract(popustDostava));
        IznosiPlacanja iznosiPlacanja = izracunajPlacanje(nacinPlacanja, trazeniIznosKarticom, ukupnaCena);

        CheckoutSummaryDTO summary = CheckoutSummaryDTO.builder()
                .stavke(stavke.stream().map(this::toCheckoutStavka).toList())
                .cenaArtikala(cenaArtikala)
                .cenaDostave(cenaDostave)
                .popustArtikli(popustArtikli)
                .popustDostava(popustDostava)
                .ukupnaCena(ukupnaCena)
                .nacinPlacanja(nacinPlacanja)
                .iznosKarticom(iznosiPlacanja.iznosKarticom())
                .iznosKes(iznosiPlacanja.iznosKes())
                .kuponId(kupon != null ? kupon.getKuponId() : null)
                .kuponKod(kupon != null ? kupon.getKod() : null)
                .poruka("Obracun je uspesno izvrsen.")
                .build();

        return new Obracun(stavke, cenaArtikala, cenaDostave, popustArtikli, popustDostava,
                ukupnaCena, iznosiPlacanja.iznosKarticom(), iznosiPlacanja.iznosKes(), kupon, summary);
    }

    private Kupon validirajKupon(Long kuponId, String kuponKod, Long kupacId) {
        Kupon kupon = kuponId != null
                ? kuponRepository.findById(kuponId).orElseThrow(() -> new RuntimeException("Kupon nije pronadjen: " + kuponId))
                : kuponRepository.findByKod(kuponKod).orElseThrow(() -> new RuntimeException("Kupon nije pronadjen: " + kuponKod));
        LocalDateTime sada = LocalDateTime.now();
        boolean valid = kupon.isAktivan();
        if (kupon.getVaziOd() != null && kupon.getVaziOd().isAfter(sada)) valid = false;
        if (kupon.getVaziDo() != null && kupon.getVaziDo().isBefore(sada)) valid = false;
        if (kupon.getMaxUpotreba() != null && kupon.getUpotrebljenoPuta() != null && kupon.getUpotrebljenoPuta() >= kupon.getMaxUpotreba()) {
            valid = false;
        }
        if (kupon.getVlasnik() == null || !kupon.getVlasnik().getKorisnikId().equals(kupacId)) {
            throw new RuntimeException("Kupon ne pripada ovom kupcu.");
        }
        if (!valid) {
            throw new RuntimeException("Kupon nije validan ili vise ne vazi: " + kupon.getKod());
        }
        return kupon;
    }

    private BigDecimal izracunajPopust(Kupon kupon, BigDecimal cenaArtikala) {
        BigDecimal popust = BigDecimal.ZERO;
        if (kupon.getPopustProcenat() != null && kupon.getPopustProcenat().compareTo(BigDecimal.ZERO) > 0) {
            popust = cenaArtikala.multiply(kupon.getPopustProcenat()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else if (kupon.getPopustIznos() != null && kupon.getPopustIznos().compareTo(BigDecimal.ZERO) > 0) {
            popust = kupon.getPopustIznos();
        }
        if (popust.compareTo(cenaArtikala) > 0) {
            return cenaArtikala;
        }
        return scale(popust);
    }

    private IznosiPlacanja izracunajPlacanje(NacinPlacanja nacinPlacanja, BigDecimal trazeniIznosKarticom, BigDecimal ukupno) {
        return switch (nacinPlacanja) {
            case KARTICA -> new IznosiPlacanja(ukupno, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            case KES -> new IznosiPlacanja(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), ukupno);
            case KOMBINOVANO -> {
                BigDecimal karticom = scale(trazeniIznosKarticom == null ? BigDecimal.ZERO : trazeniIznosKarticom);
                if (karticom.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("Iznos za placanje karticom mora biti veci od 0.");
                }
                if (karticom.compareTo(ukupno) >= 0) {
                    throw new RuntimeException("Kod kombinovanog placanja deo za kes mora biti najmanje " + MIN_KES_KOMBINOVANO + " RSD.");
                }
                BigDecimal kes = scale(ukupno.subtract(karticom));
                if (kes.compareTo(MIN_KES_KOMBINOVANO) < 0) {
                    throw new RuntimeException("Minimalni iznos placanja kesom je " + MIN_KES_KOMBINOVANO + " RSD.");
                }
                yield new IznosiPlacanja(karticom, kes);
            }
        };
    }

    private CheckoutStavkaDTO toCheckoutStavka(ObracunStavka stavka) {
        Proizvod proizvod = stavka.stavkaMenija().getProizvod();
        return CheckoutStavkaDTO.builder()
                .stavkaMenijaId(stavka.stavkaMenija().getStavkaId())
                .proizvodId(proizvod != null ? proizvod.getProizvodId() : null)
                .naziv(proizvod != null ? proizvod.getNaziv() : "Stavka menija")
                .kolicina(stavka.kolicina())
                .jedinicnaCena(stavka.jedinicnaCena())
                .ukupnaCena(stavka.ukupnaCena())
                .napomena(stavka.napomena())
                .build();
    }

    private void zabeleziKupovinu(Kupac kupac, List<Proizvod> kupljeniProizvodi) {
        LocalDateTime sada = LocalDateTime.now();
        for (Proizvod proizvod : kupljeniProizvodi) {
            klikRepository.save(Klik.builder()
                    .kupac(kupac)
                    .proizvod(proizvod)
                    .vremeKlika(sada)
                    .tipAkcije("KUPOVINA")
                    .build());
        }
    }

    private StatusPlacanja pocetniStatusPlacanja(NacinPlacanja nacinPlacanja) {
        if (nacinPlacanja == NacinPlacanja.KES) {
            return StatusPlacanja.PLACANJE_UZIVO;
        }
        return StatusPlacanja.CEKA_POTVRDU;
    }

    private void azurirajPlacanjeZaStatus(Porudzbina porudzbina, StatusPorudzbine noviStatus) {
        if (noviStatus == StatusPorudzbine.POTVRDJENA && porudzbina.getStatusPlacanja() == StatusPlacanja.CEKA_POTVRDU) {
            porudzbina.setStatusPlacanja(StatusPlacanja.NAPLACENO);
            return;
        }
        if (noviStatus == StatusPorudzbine.ISPORUCENA && porudzbina.getStatusPlacanja() == StatusPlacanja.PLACANJE_UZIVO) {
            porudzbina.setStatusPlacanja(StatusPlacanja.NAPLACENO);
            return;
        }
        if (noviStatus == StatusPorudzbine.OTKAZANA) {
            if (porudzbina.getStatusPlacanja() == StatusPlacanja.NAPLACENO) {
                porudzbina.setStatusPlacanja(StatusPlacanja.REFUNDIRANO);
            } else if (porudzbina.getStatusPlacanja() == StatusPlacanja.CEKA_POTVRDU) {
                porudzbina.setStatusPlacanja(StatusPlacanja.OTKAZANO);
            }
        }
    }

    private void oznaciKuponAkoJePlacanjeUspesno(Porudzbina porudzbina, StatusPorudzbine noviStatus) {
        Kupon kupon = porudzbina.getKupon();
        if (kupon == null || porudzbina.getStatusPlacanja() != StatusPlacanja.NAPLACENO) {
            return;
        }
        boolean karticnoPlacanjePotvrdjeno = noviStatus == StatusPorudzbine.POTVRDJENA && porudzbina.getNacinPlacanja() != NacinPlacanja.KES;
        boolean kesPlacanjeZavrseno = noviStatus == StatusPorudzbine.ISPORUCENA && porudzbina.getNacinPlacanja() == NacinPlacanja.KES;
        if (!karticnoPlacanjePotvrdjeno && !kesPlacanjeZavrseno) {
            return;
        }
        validirajKupon(kupon.getKuponId(), null, porudzbina.getKupac().getKorisnikId());
        kupon.setUpotrebljenoPuta((kupon.getUpotrebljenoPuta() == null ? 0 : kupon.getUpotrebljenoPuta()) + 1);
        if (kupon.getMaxUpotreba() != null && kupon.getUpotrebljenoPuta() >= kupon.getMaxUpotreba()) {
            kupon.setAktivan(false);
        }
        kuponRepository.save(kupon);
    }
    private void dodajIstorijuStatusa(Porudzbina porudzbina, StatusPorudzbine status, Long korisnikId) {
        porudzbina.getIstorijaStatusa().add(StatusPorudzbineIstorija.builder()
                .porudzbina(porudzbina)
                .status(status)
                .vremePromene(LocalDateTime.now())
                .promenioKorisnikId(korisnikId)
                .build());
    }

    private boolean mozeDaMenjaStatus(Korisnik korisnik, Long korisnikId, Porudzbina porudzbina, StatusPorudzbine noviStatus) {
        if (isMenadzer(korisnik) && isMenadzerZaPorudzbinu(korisnikId, porudzbina)) {
            return noviStatus == StatusPorudzbine.POTVRDJENA
                    || noviStatus == StatusPorudzbine.U_PRIPREMI
                    || noviStatus == StatusPorudzbine.SPREMNA_ZA_DOSTAVU
                    || noviStatus == StatusPorudzbine.OTKAZANA;
        }
        if (isDostavljac(korisnik)) {
            return isDostavljacZaPorudzbinu(korisnikId, porudzbina)
                    && (noviStatus == StatusPorudzbine.PREUZETA
                    || noviStatus == StatusPorudzbine.ISPORUCENA
                    || noviStatus == StatusPorudzbine.OTKAZANA);
        }
        return false;
    }

    private Korisnik getKorisnik(Long korisnikId) {
        return korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronadjen: " + korisnikId));
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
                throw new AccessDeniedException("Nemate ovlascenje za pregled ove porudzbine.");
            }
            return;
        }
        if (isMenadzer(korisnik)) {
            if (!isMenadzerZaPorudzbinu(korisnikId, porudzbina)) {
                throw new AccessDeniedException("Nemate ovlascenje za pregled ove porudzbine.");
            }
            return;
        }
        if (isDostavljac(korisnik)) {
            if (!isDostavljacZaPorudzbinu(korisnikId, porudzbina)) {
                throw new AccessDeniedException("Nemate ovlascenje za pregled ove porudzbine.");
            }
            return;
        }
        throw new AccessDeniedException("Nemate ovlascenje za pregled ove porudzbine.");
    }

    private boolean isMenadzerZaPorudzbinu(Long menadzerId, Porudzbina porudzbina) {
        return porudzbina.getStavke().stream()
                .map(StavkaPorudzbine::getStavkaMenija)
                .map(StavkaMenija::getMeni)
                .map(Meni::getRestoran)
                .filter(restoran -> restoran != null && restoran.getMenadzer() != null)
                .anyMatch(restoran -> menadzerId.equals(restoran.getMenadzer().getKorisnikId()));
    }

    private boolean isDostavljacZaPorudzbinu(Long dostavljacId, Porudzbina porudzbina) {
        return dostavaRepository.findByPorudzbinaId(porudzbina.getPorudzbinaId())
                .map(Dostava::getDostavljac)
                .map(Dostavljac::getId)
                .filter(dostavljacId::equals)
                .isPresent();
    }

    private void applyDostavljacIzDostave(Porudzbina porudzbina) {
        Long dostavljacId = dostavaRepository.findByPorudzbinaId(porudzbina.getPorudzbinaId())
                .map(Dostava::getDostavljac)
                .map(Dostavljac::getId)
                .orElse(null);
        porudzbina.setDostavljacId(dostavljacId);
    }

    private void sinhronizujDostavuZaStatus(Porudzbina porudzbina, StatusPorudzbine noviStatus) {
        if (noviStatus == StatusPorudzbine.SPREMNA_ZA_DOSTAVU) {
            dodeliDostavuAkoMoguce(porudzbina);
            return;
        }

        dostavaRepository.findByPorudzbinaId(porudzbina.getPorudzbinaId()).ifPresent(dostava -> {
            if (noviStatus == StatusPorudzbine.PREUZETA) {
                dostava.setStatus(StatusDostave.PREUZETA);
                dostava.setVremePreuzimanja(LocalDateTime.now());
                dostavaRepository.save(dostava);
                return;
            }
            if (noviStatus == StatusPorudzbine.ISPORUCENA) {
                dostava.setStatus(StatusDostave.ISPORUCENA);
                dostava.setVremeIsporuke(LocalDateTime.now());
                oslobodiDostavljaca(dostava, true);
                dostavaRepository.save(dostava);
                return;
            }
            if (noviStatus == StatusPorudzbine.OTKAZANA) {
                dostava.setStatus(StatusDostave.OTKAZANA);
                oslobodiDostavljaca(dostava, false);
                dostavaRepository.save(dostava);
            }
        });
    }

    private void dodeliDostavuAkoMoguce(Porudzbina porudzbina) {
        Dostava dostava = dostavaRepository.findByPorudzbinaId(porudzbina.getPorudzbinaId())
                .orElseGet(Dostava::new);
        if (dostava.getId() == null) {
            dostava.setPorudzbinaId(porudzbina.getPorudzbinaId());
            dostava.setVremeKreiranja(LocalDateTime.now());
            dostava.setAdresaIsporuke(porudzbina.getAdresaDostave());
            dostava.setAdresaPreuzimanja(adresaRestorana(porudzbina));
            dostava.setProcenjenoVreme(30);
        }
        if (dostava.getDostavljac() != null) {
            dostava.setStatus(StatusDostave.DODELJENA);
            dostavaRepository.save(dostava);
            return;
        }

        dostavljacRepository.findByStatus(StatusDostavljaca.DOSTUPAN).stream()
                .min(Comparator
                        .comparing((Dostavljac d) -> d.getBrojDostava() == null ? 0 : d.getBrojDostava())
                        .thenComparing(Dostavljac::getId))
                .ifPresentOrElse(dostavljac -> {
                    dostava.setDostavljac(dostavljac);
                    dostava.setStatus(StatusDostave.DODELJENA);
                    dostavljac.setStatus(StatusDostavljaca.ZAUZET);
                    dostavljacRepository.save(dostavljac);
                    dostavaRepository.save(dostava);
                }, () -> {
                    dostava.setStatus(StatusDostave.KREIRANA);
                    dostavaRepository.save(dostava);
                });
    }

    private void oslobodiDostavljaca(Dostava dostava, boolean uvecajBrojDostava) {
        Dostavljac dostavljac = dostava.getDostavljac();
        if (dostavljac == null) {
            return;
        }
        dostavljac.setStatus(StatusDostavljaca.DOSTUPAN);
        if (uvecajBrojDostava) {
            dostavljac.setBrojDostava((dostavljac.getBrojDostava() == null ? 0 : dostavljac.getBrojDostava()) + 1);
        }
        dostavljacRepository.save(dostavljac);
    }

    private String adresaRestorana(Porudzbina porudzbina) {
        return porudzbina.getStavke().stream()
                .map(StavkaPorudzbine::getStavkaMenija)
                .map(StavkaMenija::getMeni)
                .map(Meni::getRestoran)
                .filter(restoran -> restoran != null)
                .map(Restoran::getAdresa)
                .findFirst()
                .orElse("Restoran");
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private record ObracunStavka(StavkaMenija stavkaMenija, Integer kolicina, BigDecimal jedinicnaCena,
                                 BigDecimal ukupnaCena, String napomena) {
    }

    private record IznosiPlacanja(BigDecimal iznosKarticom, BigDecimal iznosKes) {
    }

    private record Obracun(List<ObracunStavka> stavke, BigDecimal cenaArtikala, BigDecimal cenaDostave,
                           BigDecimal popustArtikli, BigDecimal popustDostava, BigDecimal ukupnaCena,
                           BigDecimal iznosKarticom, BigDecimal iznosKes, Kupon kupon,
                           CheckoutSummaryDTO summary) {
    }
}
