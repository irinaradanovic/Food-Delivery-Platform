package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.menadzerAnalitika.PorudzbineAnalitikaDTO;
import fooddelivery.food_delivery_platform.model.Dostava;
import fooddelivery.food_delivery_platform.model.Kategorija;
import fooddelivery.food_delivery_platform.model.Korisnik;
import fooddelivery.food_delivery_platform.model.NacinPlacanja;
import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.model.StatusPorudzbine;
import fooddelivery.food_delivery_platform.model.StatusPorudzbineIstorija;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.model.StavkaPorudzbine;
import fooddelivery.food_delivery_platform.repository.DostavaRepository;
import fooddelivery.food_delivery_platform.repository.KorisnikRepository;
import fooddelivery.food_delivery_platform.repository.PorudzbinaRepository;
import fooddelivery.food_delivery_platform.repository.RestoranRepository;
import fooddelivery.food_delivery_platform.repository.StatusPorudzbineIstorijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PorudzbineAnalitikaService {

    private static final Set<StatusPorudzbine> AKTIVNI_STATUSI = Set.of(
            StatusPorudzbine.POTVRDJENA,
            StatusPorudzbine.U_PRIPREMI,
            StatusPorudzbine.SPREMNA_ZA_DOSTAVU,
            StatusPorudzbine.PREUZETA
    );

    private final PorudzbinaRepository porudzbinaRepository;
    private final StatusPorudzbineIstorijaRepository statusPorudzbineIstorijaRepository;
    private final DostavaRepository dostavaRepository;
    private final KorisnikRepository korisnikRepository;
    private final RestoranRepository restoranRepository;

    @Transactional(readOnly = true)
    public PorudzbineAnalitikaDTO getAnalitika(Long menadzerId, LocalDate datumOd, LocalDate datumDo, Long restoranId) {
        Korisnik korisnik = korisnikRepository.findById(menadzerId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronadjen: " + menadzerId));
        if (korisnik.getUloga() == null || !korisnik.getUloga().equalsIgnoreCase("MENADZER")) {
            throw new AccessDeniedException("Samo menadzer moze da pregleda analitiku porudzbina.");
        }

        List<Restoran> restorani = restoranRepository.findByMenadzerKorisnikId(menadzerId);
        if (restoranId != null && restorani.stream().noneMatch(r -> restoranId.equals(r.getRestoranId()))) {
            throw new AccessDeniedException("Nemate ovlascenje za analitiku ovog restorana.");
        }

        LocalDate od = datumOd != null ? datumOd : LocalDate.now().minusDays(29);
        LocalDate doDatum = datumDo != null ? datumDo : LocalDate.now();
        if (doDatum.isBefore(od)) {
            throw new RuntimeException("Datum do ne moze biti pre datuma od.");
        }

        LocalDateTime odVreme = od.atStartOfDay();
        LocalDateTime doVremeExclusive = doDatum.plusDays(1).atStartOfDay();
        List<Porudzbina> porudzbine = porudzbinaRepository.findZaAnalitikuMenadzera(menadzerId, restoranId, odVreme, doVremeExclusive);
        Map<Long, BigDecimal> prometPoPorudzbini = izracunajPrometPoPorudzbini(porudzbine);

        List<Long> porudzbinaIds = porudzbine.stream().map(Porudzbina::getPorudzbinaId).toList();
        Map<Long, List<StatusPorudzbineIstorija>> istorijaPoPorudzbini = porudzbinaIds.isEmpty()
                ? Map.of()
                : statusPorudzbineIstorijaRepository.findByPorudzbinaPorudzbinaIdInOrderByVremePromeneAsc(porudzbinaIds)
                .stream()
                .collect(Collectors.groupingBy(i -> i.getPorudzbina().getPorudzbinaId(), LinkedHashMap::new, Collectors.toList()));
        Map<Long, Dostava> dostavaPoPorudzbini = porudzbinaIds.isEmpty()
                ? Map.of()
                : dostavaRepository.findByPorudzbinaIdIn(porudzbinaIds).stream()
                .filter(d -> d.getPorudzbinaId() != null)
                .collect(Collectors.toMap(Dostava::getPorudzbinaId, Function.identity(), (a, b) -> a));

        BigDecimal promet = prometPoPorudzbini.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        long ukupno = porudzbine.size();
        long otkazane = porudzbine.stream().filter(p -> p.getStatus() == StatusPorudzbine.OTKAZANA).count();
        long isporucene = porudzbine.stream().filter(p -> p.getStatus() == StatusPorudzbine.ISPORUCENA).count();
        long aktivne = porudzbine.stream().filter(p -> AKTIVNI_STATUSI.contains(p.getStatus())).count();
        BigDecimal prosecna = ukupno == 0 ? BigDecimal.ZERO : promet.divide(BigDecimal.valueOf(ukupno), 2, RoundingMode.HALF_UP);

        return PorudzbineAnalitikaDTO.builder()
                .datumOd(od)
                .datumDo(doDatum)
                .restoranId(restoranId)
                .restoranNaziv(nazivRestorana(restorani, restoranId))
                .restorani(restorani.stream()
                        .sorted(Comparator.comparing(Restoran::getNaziv, Comparator.nullsLast(String::compareToIgnoreCase)))
                        .map(r -> PorudzbineAnalitikaDTO.RestoranFilterDTO.builder()
                                .restoranId(r.getRestoranId())
                                .naziv(r.getNaziv())
                                .build())
                        .toList())
                .sazetak(PorudzbineAnalitikaDTO.SazetakPorudzbinaDTO.builder()
                        .ukupanBrojPorudzbina(ukupno)
                        .aktivnePorudzbine(aktivne)
                        .isporucenePorudzbine(isporucene)
                        .otkazanePorudzbine(otkazane)
                        .prometArtikala(scale(promet))
                        .prosecnaVrednostPorudzbine(prosecna)
                        .stopaOtkazivanja(ukupno == 0 ? 0.0 : round1(((double) otkazane / ukupno) * 100))
                        .prosecnoVremeDoPotvrdeMin(prosekTrajanjaDoStatusa(porudzbine, istorijaPoPorudzbini, StatusPorudzbine.POTVRDJENA))
                        .prosecnoVremeDoIsporukeMin(prosekDoIsporuke(porudzbine, istorijaPoPorudzbini, dostavaPoPorudzbini))
                        .build())
                .kuponi(kuponi(porudzbine))
                .dnevniTrend(dnevniTrend(od, doDatum, porudzbine, prometPoPorudzbini))
                .statusi(statusi(porudzbine))
                .topStavke(topStavke(porudzbine))
                .satniTrend(satniTrend(porudzbine))
                .placanja(placanja(porudzbine, prometPoPorudzbini))
                .nedavnePorudzbine(nedavnePorudzbine(porudzbine, prometPoPorudzbini))
                .build();
    }

    private Map<Long, BigDecimal> izracunajPrometPoPorudzbini(List<Porudzbina> porudzbine) {
        Map<Long, BigDecimal> rezultat = new HashMap<>();
        for (Porudzbina porudzbina : porudzbine) {
            BigDecimal promet = porudzbina.getStavke().stream()
                    .map(StavkaPorudzbine::getCena)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            rezultat.put(porudzbina.getPorudzbinaId(), scale(promet));
        }
        return rezultat;
    }

    private PorudzbineAnalitikaDTO.KuponiPorudzbinaDTO kuponi(List<Porudzbina> porudzbine) {
        long brojSaKuponom = porudzbine.stream().filter(p -> p.getKupon() != null).count();
        BigDecimal popust = porudzbine.stream()
                .map(p -> nz(p.getPopustArtikli()).add(nz(p.getPopustDostava())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return PorudzbineAnalitikaDTO.KuponiPorudzbinaDTO.builder()
                .brojPorudzbinaSaKuponom(brojSaKuponom)
                .ukupanPopust(scale(popust))
                .build();
    }

    private List<PorudzbineAnalitikaDTO.DnevniTrendDTO> dnevniTrend(LocalDate od, LocalDate datumDo, List<Porudzbina> porudzbine,
                                                                     Map<Long, BigDecimal> prometPoPorudzbini) {
        Map<LocalDate, List<Porudzbina>> poDanu = porudzbine.stream()
                .filter(p -> p.getDatumKreiranja() != null)
                .collect(Collectors.groupingBy(p -> p.getDatumKreiranja().toLocalDate()));
        List<PorudzbineAnalitikaDTO.DnevniTrendDTO> rezultat = new ArrayList<>();
        for (LocalDate datum = od; !datum.isAfter(datumDo); datum = datum.plusDays(1)) {
            List<Porudzbina> dnevne = poDanu.getOrDefault(datum, List.of());
            BigDecimal promet = dnevne.stream()
                    .map(p -> prometPoPorudzbini.getOrDefault(p.getPorudzbinaId(), BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            rezultat.add(PorudzbineAnalitikaDTO.DnevniTrendDTO.builder()
                    .datum(datum)
                    .brojPorudzbina(dnevne.size())
                    .promet(scale(promet))
                    .build());
        }
        return rezultat;
    }

    private List<PorudzbineAnalitikaDTO.StatusPorudzbinaDTO> statusi(List<Porudzbina> porudzbine) {
        Map<StatusPorudzbine, Long> brojPoStatusu = porudzbine.stream()
                .filter(p -> p.getStatus() != null)
                .collect(Collectors.groupingBy(Porudzbina::getStatus, () -> new EnumMap<>(StatusPorudzbine.class), Collectors.counting()));
        List<PorudzbineAnalitikaDTO.StatusPorudzbinaDTO> rezultat = new ArrayList<>();
        for (StatusPorudzbine status : StatusPorudzbine.values()) {
            rezultat.add(PorudzbineAnalitikaDTO.StatusPorudzbinaDTO.builder()
                    .status(status)
                    .brojPorudzbina(brojPoStatusu.getOrDefault(status, 0L))
                    .build());
        }
        return rezultat;
    }

    private List<PorudzbineAnalitikaDTO.TopStavkaDTO> topStavke(List<Porudzbina> porudzbine) {
        Map<Long, StavkaAkumulator> poStavci = new HashMap<>();
        for (Porudzbina porudzbina : porudzbine) {
            for (StavkaPorudzbine stavka : porudzbina.getStavke()) {
                StavkaMenija stavkaMenija = stavka.getStavkaMenija();
                Long key = stavkaMenija != null ? stavkaMenija.getStavkaId() : stavka.getStavkaId();
                StavkaAkumulator acc = poStavci.computeIfAbsent(key, ignored -> new StavkaAkumulator(stavkaMenija));
                acc.kolicina += stavka.getKolicina() == null ? 0 : stavka.getKolicina();
                acc.promet = acc.promet.add(nz(stavka.getCena()));
            }
        }
        return poStavci.values().stream()
                .sorted(Comparator.comparing((StavkaAkumulator a) -> a.promet).reversed())
                .limit(10)
                .map(StavkaAkumulator::toDto)
                .toList();
    }

    private List<PorudzbineAnalitikaDTO.SatniTrendDTO> satniTrend(List<Porudzbina> porudzbine) {
        Map<Integer, Long> poSatu = porudzbine.stream()
                .filter(p -> p.getDatumKreiranja() != null)
                .collect(Collectors.groupingBy(p -> p.getDatumKreiranja().getHour(), Collectors.counting()));
        List<PorudzbineAnalitikaDTO.SatniTrendDTO> rezultat = new ArrayList<>();
        for (int sat = 0; sat < 24; sat++) {
            rezultat.add(PorudzbineAnalitikaDTO.SatniTrendDTO.builder()
                    .sat(sat)
                    .brojPorudzbina(poSatu.getOrDefault(sat, 0L))
                    .build());
        }
        return rezultat;
    }

    private List<PorudzbineAnalitikaDTO.PlacanjeTrendDTO> placanja(List<Porudzbina> porudzbine, Map<Long, BigDecimal> prometPoPorudzbini) {
        Map<NacinPlacanja, List<Porudzbina>> poPlacanju = porudzbine.stream()
                .filter(p -> p.getNacinPlacanja() != null)
                .collect(Collectors.groupingBy(Porudzbina::getNacinPlacanja, () -> new EnumMap<>(NacinPlacanja.class), Collectors.toList()));
        List<PorudzbineAnalitikaDTO.PlacanjeTrendDTO> rezultat = new ArrayList<>();
        for (NacinPlacanja placanje : NacinPlacanja.values()) {
            List<Porudzbina> lista = poPlacanju.getOrDefault(placanje, List.of());
            BigDecimal promet = lista.stream()
                    .map(p -> prometPoPorudzbini.getOrDefault(p.getPorudzbinaId(), BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            rezultat.add(PorudzbineAnalitikaDTO.PlacanjeTrendDTO.builder()
                    .nacinPlacanja(placanje)
                    .brojPorudzbina(lista.size())
                    .promet(scale(promet))
                    .build());
        }
        return rezultat;
    }

    private List<PorudzbineAnalitikaDTO.NedavnaPorudzbinaDTO> nedavnePorudzbine(List<Porudzbina> porudzbine,
                                                                                 Map<Long, BigDecimal> prometPoPorudzbini) {
        return porudzbine.stream()
                .sorted(Comparator.comparing(Porudzbina::getDatumKreiranja, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(12)
                .map(p -> PorudzbineAnalitikaDTO.NedavnaPorudzbinaDTO.builder()
                        .porudzbinaId(p.getPorudzbinaId())
                        .datumKreiranja(p.getDatumKreiranja())
                        .status(p.getStatus())
                        .nacinPlacanja(p.getNacinPlacanja())
                        .prometMenadzera(prometPoPorudzbini.getOrDefault(p.getPorudzbinaId(), BigDecimal.ZERO))
                        .brojStavki(p.getStavke().stream().mapToLong(s -> s.getKolicina() == null ? 0 : s.getKolicina()).sum())
                        .build())
                .toList();
    }

    private Double prosekTrajanjaDoStatusa(List<Porudzbina> porudzbine,
                                           Map<Long, List<StatusPorudzbineIstorija>> istorijaPoPorudzbini,
                                           StatusPorudzbine status) {
        List<Long> minuti = porudzbine.stream()
                .map(p -> minutiOdKreiranjaDoStatusa(p, istorijaPoPorudzbini.getOrDefault(p.getPorudzbinaId(), List.of()), status))
                .filter(Objects::nonNull)
                .toList();
        return prosekMinuta(minuti);
    }

    private Double prosekDoIsporuke(List<Porudzbina> porudzbine,
                                    Map<Long, List<StatusPorudzbineIstorija>> istorijaPoPorudzbini,
                                    Map<Long, Dostava> dostavaPoPorudzbini) {
        List<Long> minuti = porudzbine.stream()
                .map(p -> {
                    LocalDateTime isporuka = null;
                    Dostava dostava = dostavaPoPorudzbini.get(p.getPorudzbinaId());
                    if (dostava != null) {
                        isporuka = dostava.getVremeIsporuke();
                    }
                    if (isporuka == null) {
                        isporuka = vremeStatusa(istorijaPoPorudzbini.getOrDefault(p.getPorudzbinaId(), List.of()), StatusPorudzbine.ISPORUCENA);
                    }
                    if (p.getDatumKreiranja() == null || isporuka == null) return null;
                    return Duration.between(p.getDatumKreiranja(), isporuka).toMinutes();
                })
                .filter(Objects::nonNull)
                .toList();
        return prosekMinuta(minuti);
    }

    private Long minutiOdKreiranjaDoStatusa(Porudzbina porudzbina, List<StatusPorudzbineIstorija> istorija, StatusPorudzbine status) {
        LocalDateTime vremeStatusa = vremeStatusa(istorija, status);
        if (porudzbina.getDatumKreiranja() == null || vremeStatusa == null) return null;
        return Duration.between(porudzbina.getDatumKreiranja(), vremeStatusa).toMinutes();
    }

    private LocalDateTime vremeStatusa(List<StatusPorudzbineIstorija> istorija, StatusPorudzbine status) {
        return istorija.stream()
                .filter(i -> i.getStatus() == status)
                .map(StatusPorudzbineIstorija::getVremePromene)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Double prosekMinuta(List<Long> minuti) {
        if (minuti.isEmpty()) return null;
        double avg = minuti.stream().mapToLong(Long::longValue).average().orElse(0.0);
        return round1(avg);
    }

    private String nazivRestorana(List<Restoran> restorani, Long restoranId) {
        if (restoranId == null) return "Svi restorani";
        return restorani.stream()
                .filter(r -> restoranId.equals(r.getRestoranId()))
                .map(Restoran::getNaziv)
                .findFirst()
                .orElse("Restoran #" + restoranId);
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal scale(BigDecimal value) {
        return nz(value).setScale(2, RoundingMode.HALF_UP);
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static class StavkaAkumulator {
        private final Long stavkaMenijaId;
        private final Long proizvodId;
        private final String naziv;
        private final String kategorija;
        private long kolicina;
        private BigDecimal promet = BigDecimal.ZERO;

        private StavkaAkumulator(StavkaMenija stavkaMenija) {
            this.stavkaMenijaId = stavkaMenija != null ? stavkaMenija.getStavkaId() : null;
            Proizvod proizvod = stavkaMenija != null ? stavkaMenija.getProizvod() : null;
            Kategorija kat = proizvod != null ? proizvod.getKategorija() : null;
            this.proizvodId = proizvod != null ? proizvod.getProizvodId() : null;
            this.naziv = proizvod != null && proizvod.getNaziv() != null ? proizvod.getNaziv() : "Stavka menija";
            this.kategorija = kat != null ? kat.getNaziv() : "Bez kategorije";
        }

        private PorudzbineAnalitikaDTO.TopStavkaDTO toDto() {
            BigDecimal prosecnaCena = kolicina == 0 ? BigDecimal.ZERO : promet.divide(BigDecimal.valueOf(kolicina), 2, RoundingMode.HALF_UP);
            return PorudzbineAnalitikaDTO.TopStavkaDTO.builder()
                    .stavkaMenijaId(stavkaMenijaId)
                    .proizvodId(proizvodId)
                    .naziv(naziv)
                    .kategorija(kategorija)
                    .kolicina(kolicina)
                    .promet(promet.setScale(2, RoundingMode.HALF_UP))
                    .prosecnaCena(prosecnaCena)
                    .build();
        }
    }
}

