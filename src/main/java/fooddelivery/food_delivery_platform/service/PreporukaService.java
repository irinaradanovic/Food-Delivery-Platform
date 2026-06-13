package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreporukaService {

    private final KlikRepository klikRepository;
    private final PretragaRepository pretragaRepository;
    private final PorudzbinaRepository porudzbinaRepository;
    private final StavkaPorudzbineRepository stavkaPorudzbineRepository;
    private final ProizvodRepository proizvodRepository;

    // Tezinski faktori za razlicite akcije
    private static final Map<String, Double> TEZINE_AKCIJA = Map.of(
            "KUPOVINA",          10.0,
            "PLACANJE",           8.0,
            "KORPA",              5.0,
            "DODAJ_OMILJENI",     4.0,
            "DETALJI",            2.0,
            "REZULTAT_PRETRAGE",  1.5,
            "PREGLED",            1.0
    );

    public List<Proizvod> getPersonalizovanePreporuke(Long kupacId, int limit) {

        // Mapa: proizvodId -> ukupni skor
        Map<Long, Double> skorovi = new HashMap<>();

        List<Proizvod> sviProizvodi = proizvodRepository.findAll();

        // --- 1. Iz porudzbina (najveci tezinski faktor) ---
        // Narudjeni proizvodi dobijaju visoki skor jer je to najjaci signal interesovanja.
        // Ponavljanje iste narudzbine je norma za hranu — ne skrivamo vec narudjene.
        List<Porudzbina> porudzbine = porudzbinaRepository
                .findByKupac_KorisnikIdOrderByDatumKreiranjaDesc(kupacId);

        for (Porudzbina p : porudzbine) {
            List<StavkaPorudzbine> stavke = stavkaPorudzbineRepository
                    .findByPorudzbinaPorudzbinaId(p.getPorudzbinaId());

            for (StavkaPorudzbine stavka : stavke) {
                if (stavka.getStavkaMenija() == null) continue;
                Proizvod proizvod = stavka.getStavkaMenija().getProizvod();
                if (proizvod == null) continue;

                Long pid = proizvod.getProizvodId();
                // Veci skor za veci broj narudjenih komada
                double bonusKolicina = stavka.getKolicina() != null ? stavka.getKolicina() : 1;
                skorovi.merge(pid, 10.0 * bonusKolicina, Double::sum);

                // Svi proizvodi iste kategorije dobijaju manji bonus
                if (proizvod.getKategorija() != null) {
                    bonusSrodnimProizvodima(
                            proizvod.getKategorija().getKategorijaId(),
                            skorovi, sviProizvodi, 3.0, pid
                    );
                }
            }
        }

        // --- 2. Iz klikova ---
        List<Klik> klikovi = klikRepository
                .findByKupac_KorisnikIdOrderByVremeKlikaDesc(kupacId);

        for (Klik klik : klikovi) {
            if (klik.getProizvod() != null) {
                Long pid = klik.getProizvod().getProizvodId();
                double tezina = TEZINE_AKCIJA.getOrDefault(klik.getTipAkcije(), 1.0);
                skorovi.merge(pid, tezina, Double::sum);

                // Srodnim proizvodima iste kategorije manji bonus
                if (klik.getProizvod().getKategorija() != null) {
                    bonusSrodnimProizvodima(
                            klik.getProizvod().getKategorija().getKategorijaId(),
                            skorovi, sviProizvodi, 0.5, pid
                    );
                }
            }

            // Klik na kategoriju boduje sve proizvode te kategorije
            if (klik.getKategorija() != null) {
                bonusSrodnimProizvodima(
                        klik.getKategorija().getKategorijaId(),
                        skorovi, sviProizvodi, 1.0, null
                );
            }
        }

        // --- 3. Iz pretraga ---
        List<Pretraga> pretrage = pretragaRepository
                .findByKupac_KorisnikIdOrderByVremePretrageDesc(kupacId);

        for (Pretraga pretraga : pretrage) {
            if (pretraga.getTekstUpita() == null) continue;
            String upit = pretraga.getTekstUpita().toLowerCase().trim();
            if (upit.length() < 2) continue;

            for (Proizvod p : sviProizvodi) {
                boolean poklapanje = p.getNaziv() != null
                        && p.getNaziv().toLowerCase().contains(upit);
                if (!poklapanje && p.getKategorija() != null) {
                    poklapanje = p.getKategorija().getNaziv().toLowerCase().contains(upit);
                }
                if (poklapanje) {
                    skorovi.merge(p.getProizvodId(), 1.5, Double::sum);
                }
            }
        }

        // --- 4. Ako kupac nema istoriju, vrati najpopularnije po broju klikova ---
        if (skorovi.isEmpty()) {
            return sviProizvodi.stream().limit(limit).collect(Collectors.toList());
        }

        // --- 5. Sortiranje po skoru i vracanje top limit rezultata ---
        Map<Long, Proizvod> proizvodiMap = sviProizvodi.stream()
                .collect(Collectors.toMap(Proizvod::getProizvodId, p -> p));

        return skorovi.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(e -> proizvodiMap.get(e.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Dodaje bonus svim proizvodima iste kategorije, osim izuzetog proizvoda
    private void bonusSrodnimProizvodima(Long katId, Map<Long, Double> skorovi,
                                         List<Proizvod> sviProizvodi,
                                         double bonus, Long izuzetProizvodId) {
        sviProizvodi.stream()
                .filter(p -> p.getKategorija() != null
                        && p.getKategorija().getKategorijaId().equals(katId)
                        && (izuzetProizvodId == null || !p.getProizvodId().equals(izuzetProizvodId)))
                .forEach(p -> skorovi.merge(p.getProizvodId(), bonus, Double::sum));
    }

    // Sezonske preporuke - proizvodi iz SezonskiMeni čija sezona je aktuelna
    public List<Proizvod> getSezonskiProizvodi(int limit) {
        return proizvodRepository.findSezonskiProizvodi(LocalDate.now())
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Vremenske preporuke - proizvodi iz VremenskiMeni čiji interval pokriva trenutno vreme
    public List<Proizvod> getVremenskiProizvodi(int limit) {
        return proizvodRepository.findVremenskiProizvodi(LocalTime.now())
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Trend preporuke - najklikavaniji proizvodi u poslednjih 7 dana (globalno)
    public List<Proizvod> getTrendProizvodi(int limit) {
        LocalDateTime od = LocalDateTime.now().minusDays(7);
        List<Klik> klikovi = klikRepository.findKlikoviNaProizvodimaOd(od);

        // Težinski skor po tipu akcije, isto kao personalizovane preporuke
        Map<Long, Double> skorovi = new HashMap<>();
        for (Klik k : klikovi) {
            if (k.getProizvod() == null) continue;
            double tezina = TEZINE_AKCIJA.getOrDefault(k.getTipAkcije(), 1.0);
            skorovi.merge(k.getProizvod().getProizvodId(), tezina, Double::sum);
        }

        if (skorovi.isEmpty()) {
            // Fallback: prvih N proizvoda iz baze
            return proizvodRepository.findAll().stream().limit(limit).collect(Collectors.toList());
        }

        Map<Long, Proizvod> proizvodiMap = new HashMap<>();
        klikovi.stream()
                .filter(k -> k.getProizvod() != null)
                .forEach(k -> proizvodiMap.put(k.getProizvod().getProizvodId(), k.getProizvod()));

        return skorovi.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(e -> proizvodiMap.get(e.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}