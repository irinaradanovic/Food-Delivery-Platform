package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.*;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StavkaMenijaService {

    @Autowired
    private StavkaMenijaRepository stavkaMenijaRepository;

    @Autowired
    private MeniRepository meniRepository;

    @Autowired
    private ProizvodRepository proizvodRepository;

    @Autowired
    private KategorijaRepository kategorijaRepository;

    @Autowired
    private SastojakRepository sastojakRepository;

    @Autowired
    private AlergenRepository alergenRepository;

    @Autowired
    private MeniService meniService;

    private final String UPLOAD_DIR = "src/main/resources/static/images/food/";

    @Transactional(readOnly = true)
    public List<StavkaMenija> getItemsByMenu(Long meniId) {
        return stavkaMenijaRepository.findByMeniMeniIdAndObrisanFalse(meniId);
    }

    public StavkaMenija getItemById(Long id) {
        return stavkaMenijaRepository.findById(id).orElse(null);
    }


    public List<StavkaMenija> getItemsByMenuForKupac(Long meniId) {
        Meni meni = meniRepository.findById(meniId)
                .orElseThrow(() -> new EntityNotFoundException("Meni sa ID-jem " + meniId + " ne postoji."));
        if (!meni.isAktivan()) {
            throw new AccessDeniedException("Trazeni meni nije aktivan.");
        }
        return stavkaMenijaRepository.findByMeniMeniIdAndObrisanFalse(meniId);
    }

    @Transactional(readOnly = true)
    public List<MeniSaStavkamaDTO> getAktivneStavkeZaRestoranKupac(Long restoranId) {
        List<Meni> aktivniMeniji = meniRepository.findByRestoranRestoranIdAndAktivanTrue(restoranId);
        List<StavkaMenija> aktivneStavkeRestorana = stavkaMenijaRepository.findAktivneStavkeZaRestoran(restoranId);
        // grupisi stavke po meniId
        Map<Long, List<StavkaMenija>> stavkePoMeniju = aktivneStavkeRestorana.stream()
                .collect(Collectors.groupingBy(s -> s.getMeni().getMeniId()));

        return aktivniMeniji.stream().map(meni -> {
            MeniSaStavkamaDTO dto = new MeniSaStavkamaDTO();
            dto.setMeniId(meni.getMeniId());
            dto.setNaziv(meni.getNaziv());
            dto.setOpis(meni.getOpis());

            // ako je vremenski ili sezonski, popuni info
            if (meni instanceof VremenskiMeni v) {
                dto.setVremeOd(v.getVremeOd());
                dto.setVremeDo(v.getVremeDo());
            } else if (meni instanceof SezonskiMeni s) {
                dto.setPocetakSezone(s.getPocetakSezone());
                dto.setKrajSezone(s.getKrajSezone());
            }
            List<StavkaMenija> stavkeZaOvajMeni = stavkePoMeniju.getOrDefault(meni.getMeniId(), new ArrayList<>());

            List<KupacStavkaMenijaDTO> mapiraneStavke = stavkeZaOvajMeni.stream()
                    .filter(stavka -> stavka.isDostupno() && !stavka.isObrisan())
                    .map(this::toKupacStavkaMenijaDTO)
                    .toList();

            dto.setStavke(mapiraneStavke);
            return dto;
        }).toList();

    }

    private KupacStavkaMenijaDTO toKupacStavkaMenijaDTO(StavkaMenija stavka) {
        Proizvod proizvod = stavka.getProizvod();
        return KupacStavkaMenijaDTO.builder()
                .stavkaMenijaId(stavka.getStavkaId())
                .proizvodId(proizvod != null ? proizvod.getProizvodId() : null)
                .naziv(proizvod != null ? proizvod.getNaziv() : null)
                .opis(proizvod != null ? proizvod.getOpis() : null)
                .kalorije(proizvod != null ? proizvod.getKalorije() : null)
                .cena(stavka.getCena())
                .fotografija(proizvod != null ? proizvod.getFotografija() : null)
                .kolicina(proizvod != null ? proizvod.getKolicina() : null)
                .mernaJedinica(proizvod != null ? proizvod.getMernaJedinica() : null)
                .kategorija(proizvod != null ? proizvod.getKategorija() : null)
                .alergeni(proizvod != null ? proizvod.getAlergeni() : List.of())
                .sastojci(proizvod != null ? proizvod.getSastojci() : List.of())
                .vremePripremeMin(stavka.getVremePripremeMin())
                .vremePripremeMax(stavka.getVremePripremeMax())
                .dostupno(stavka.isDostupno())
                .build();
    }

    @Transactional
    public Long addMenuItem(Long meniId, Long trenutniKorisnikId, NovaStavkaMenijaDTO request, MultipartFile slika) throws IOException {
        Meni meni = meniRepository.findById(meniId)
                .orElseThrow(() -> new IllegalArgumentException("Meni ne postoji."));

        checkAccess(meniRepository.findById(meniId).orElseThrow(), trenutniKorisnikId);

        String putanjaSlikeUBazi = "images/food/default.jpg";
        if (slika != null && !slika.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // generisanje unikatnog imena fajla kako bi se izbeglo pregazenje slika
            String jedinstvenoIme = UUID.randomUUID().toString() + "_" + slika.getOriginalFilename();
            Path fajlPutanja = uploadPath.resolve(jedinstvenoIme);
            Files.copy(slika.getInputStream(), fajlPutanja, StandardCopyOption.REPLACE_EXISTING);
            putanjaSlikeUBazi = "images/food/" + jedinstvenoIme;
        }

        // korisnik moze da bira kategorije ili da unese novu
        Kategorija kategorija = null;
        if (request.getKategorijaId() != null) {
            kategorija = kategorijaRepository.findById(request.getKategorijaId()).orElse(null);
        } else if (request.getNovaKategorijaNaziv() != null && !request.getNovaKategorijaNaziv().trim().isEmpty()) {
            kategorija = Kategorija.builder()
                    .naziv(request.getNovaKategorijaNaziv().trim())
                    .kreiraoKorisnikId(trenutniKorisnikId)
                    .build();
            kategorija = kategorijaRepository.save(kategorija);
        }


        List<Sastojak> konacniSastojci = new ArrayList<>();
        if (request.getSastojciIds() != null) {
            konacniSastojci.addAll(sastojakRepository.findAllById(request.getSastojciIds()));
        }
        if (request.getNoviSastojciNazivi() != null) {
            for (String naziv : request.getNoviSastojciNazivi()) {
                if (!naziv.trim().isEmpty()) {
                    Sastojak s = Sastojak.builder().naziv(naziv.trim()).kreiraoKorisnikId(trenutniKorisnikId).build();
                    konacniSastojci.add(sastojakRepository.save(s));
                }
            }
        }

        List<Alergen> konacniAlergeni = new ArrayList<>();
        if (request.getAlergeniIds() != null) {
            konacniAlergeni.addAll(alergenRepository.findAllById(request.getAlergeniIds()));
        }


        Proizvod proizvod = Proizvod.builder()
                .naziv(request.getNaziv())
                .opis(request.getOpis())
                .kalorije(request.getKalorije())
                .cena(request.getCena()) // Osnovna cena na proizvodu
                .fotografija(putanjaSlikeUBazi)
                .kolicina(request.getKolicina())
                .mernaJedinica(request.getMernaJedinica())
                .kategorija(kategorija)
                .sastojci(konacniSastojci)
                .alergeni(konacniAlergeni)
                .build();
        proizvod = proizvodRepository.save(proizvod);


        // nova verzija pri dodavanju stavki
        Meni noviMeni = meniService.cloneMenu(meni);
        String novaVerzija = meniService.findNextVersion(meni.getGrupniMeniId());
        noviMeni.setVerzija(novaVerzija);
        noviMeni.setAktivan(true);
        noviMeni.setDatumOd(LocalDate.now()); // datum pocetka aktivacije menija

        meniRepository.save(noviMeni);
        meniRepository.flush();
        // okida se triger koji deaktivira staru verziju

        meniService.copyMenuItems(meni.getMeniId(), noviMeni);

        StavkaMenija stavkaMenija = StavkaMenija.builder()
                .meni(noviMeni)
                .proizvod(proizvod)
                .vremePripremeMin(request.getVremePripremeMin())
                .vremePripremeMax(request.getVremePripremeMax())
                .cena(request.getCena())
                .dostupno(true)
                .obrisan(false)
                .build();
        stavkaMenijaRepository.save(stavkaMenija);
        stavkaMenijaRepository.flush();

        return noviMeni.getMeniId();


    }


    @Transactional
    public void deleteItem(Long meniId, Long stavkaId, Long trenutniKorisnikId) {
        StavkaMenija stavka = stavkaMenijaRepository.findById(stavkaId)
                .orElseThrow(() -> new EntityNotFoundException("Stavka menija ne postoji."));

        if (!stavka.getMeni().getMeniId().equals(meniId)) {
            throw new IllegalArgumentException("Ova stavka ne pripada izabranom meniju!");
        }

        checkAccess(meniRepository.findById(meniId).orElseThrow(), trenutniKorisnikId);

        stavka.setObrisan(true);
        stavkaMenijaRepository.save(stavka);
    }

    @Transactional
    public void updateMenuItem(Long meniId, Long stavkaId, IzmenaStavkeMenijaDTO dto, MultipartFile slika , Long trenutniKorisnikId) {
        StavkaMenija stavka = stavkaMenijaRepository.findById(stavkaId)
                .orElseThrow(() -> new EntityNotFoundException("Stavka ne postoji"));

        if (!stavka.getMeni().getMeniId().equals(meniId)) {
            throw new IllegalArgumentException("Ova stavka ne pripada izabranom meniju!");
        }


        checkAccess(meniRepository.findById(meniId).orElseThrow(), trenutniKorisnikId);

        Proizvod proizvod = stavka.getProizvod();

        // Ažuriranje primitivnih polja proizvoda i stavke
        proizvod.setNaziv(dto.getNaziv());
        proizvod.setOpis(dto.getOpis());
        proizvod.setKalorije(dto.getKalorije());
        proizvod.setKolicina(dto.getKolicina());
        proizvod.setMernaJedinica(dto.getMernaJedinica());
        proizvod.setCena(dto.getCena());

        stavka.setCena(dto.getCena());
        stavka.setVremePripremeMin(dto.getVremePripremeMin());
        stavka.setVremePripremeMax(dto.getVremePripremeMax());

        // Obrada Kategorije
        if (dto.getNovaKategorijaNaziv() != null && !dto.getNovaKategorijaNaziv().isBlank()) {
            Kategorija novaKat = Kategorija.builder()
                    .naziv(dto.getNovaKategorijaNaziv().trim())
                    .kreiraoKorisnikId(trenutniKorisnikId)
                    .build();
            novaKat = kategorijaRepository.save(novaKat);
            proizvod.setKategorija(novaKat);
        } else if (dto.getKategorijaId() != null) {
            Kategorija kat = kategorijaRepository.findById(dto.getKategorijaId())
                    .orElseThrow(() -> new EntityNotFoundException("Kategorija nije pronađena"));
            proizvod.setKategorija(kat);
        }

        proizvod.getSastojci().clear();
        if (dto.getSastojciIds() != null) {
            proizvod.getSastojci().addAll(sastojakRepository.findAllById(dto.getSastojciIds()));
        }
        if (dto.getNoviSastojciNazivi() != null) {
            for (String naziv : dto.getNoviSastojciNazivi()) {
                if (!naziv.trim().isEmpty()) {
                    Sastojak s = Sastojak.builder()
                            .naziv(naziv.trim())
                            .kreiraoKorisnikId(trenutniKorisnikId)
                            .build();
                    proizvod.getSastojci().add(sastojakRepository.save(s));
                }
            }
        }

        proizvod.getAlergeni().clear();
        if (dto.getAlergeniIds() != null) {
            proizvod.getAlergeni().addAll(alergenRepository.findAllById(dto.getAlergeniIds()));
        }

        if (slika != null && !slika.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String jedinstvenoIme = UUID.randomUUID().toString() + "_" + slika.getOriginalFilename();
                Path fajlPutanja = uploadPath.resolve(jedinstvenoIme);
                Files.copy(slika.getInputStream(), fajlPutanja, StandardCopyOption.REPLACE_EXISTING);

                String putanjaSlikeUBazi = "images/food/" + jedinstvenoIme;
                proizvod.setFotografija(putanjaSlikeUBazi);
            } catch (IOException e) {
                throw new RuntimeException("Greška prilikom čuvanja fotografije proizvoda.", e);
            }
        }

        proizvodRepository.save(proizvod);
        stavkaMenijaRepository.save(stavka);
    }

    @Transactional
    public Meni updateMenuPriceListWithVersioning(Long stariMeniId, CenovnikMasovniUpdateDTO dto, Long trenutniKorisnikId) {
        Meni stariMeni = meniRepository.findById(stariMeniId)
                .orElseThrow(() -> new EntityNotFoundException("Meni sa ID-jem " + stariMeniId + " ne postoji."));
        checkAccess(meniRepository.findById(stariMeniId).orElseThrow(), trenutniKorisnikId);

        // azuriramo cene koje su se zapravo promenile
       /* for (var izmena : dto.getIzmeneCena()) {
            StavkaMenija stavka = stavkaMenijaRepository.findById(izmena.getStavkaId()).orElseThrow();
            stavka.setCena(izmena.getNovaCena());
            stavkaMenijaRepository.save(stavka);
        }
        //  guramo izmene cena u bazu pre kreiranja menija
        stavkaMenijaRepository.flush();  */

        Meni noviMeni = meniService.cloneMenu(stariMeni);
        String novaVerzija = meniService.findNextVersion(stariMeni.getGrupniMeniId());
        noviMeni.setVerzija(novaVerzija);
        noviMeni.setAktivan(true);
        noviMeni.setDatumOd(LocalDate.now()); // datum pocetka aktivacije menija

        meniRepository.save(noviMeni);
        meniRepository.flush();
        // okida se triger koji kopira sve stavke sa novim cenama

        meniService.copyMenuItems(stariMeni.getMeniId(), noviMeni);
        stavkaMenijaRepository.flush();

        for (var izmena : dto.getIzmeneCena()) {
            StavkaMenija novaStavka = stavkaMenijaRepository
                    .findByMeniAndProizvod(noviMeni, stavkaMenijaRepository.findById(izmena.getStavkaId()).get().getProizvod())
                    .orElseThrow();

            novaStavka.setCena(izmena.getNovaCena());
            stavkaMenijaRepository.save(novaStavka);
        }
        return noviMeni;
    }

    private void checkAccess(Meni meni, Long korisnikId) {
        Restoran restoran = meni.getRestoran();
        if (restoran == null || !restoran.getMenadzer().getKorisnikId().equals(korisnikId)) {
            throw new AccessDeniedException("Nemate ovlašćenje da menjate ovaj meni!");
        }
    }


    public Map<String, Integer> calculateAvgPreparationTime(Long kategorijaId) {
        Map<String, Integer> mapa = new HashMap<>();

        Double min = stavkaMenijaRepository.findAvgMinByKategorija(kategorijaId);
        Double max = stavkaMenijaRepository.findAvgMaxByKategorija(kategorijaId);

        if (min != null && max != null && min > 0 && max > 0) {
            mapa.put("min", (int) Math.round(min));
            mapa.put("max", (int) Math.round(max));
        } else {
            mapa.put("min", 15);
            mapa.put("max", 25);
        }

        return mapa;
    }
}

