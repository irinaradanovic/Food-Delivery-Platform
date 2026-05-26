package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.NovaStavkaMenijaDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.io.IOException;
import java.util.List;

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

    private final String UPLOAD_DIR = "src/main/resources/static/images/food/";

    @Transactional(readOnly = true)
    public List<StavkaMenija> getItemsByMenu(Long meniId) {
        return stavkaMenijaRepository.findByMeniMeniIdAndObrisanFalse(meniId);
    }



    @Transactional
    public void addMenuItem(Long meniId, Long trenutniKorisnikId, NovaStavkaMenijaDTO request, MultipartFile slika) throws IOException {
        Meni meni = meniRepository.findById(meniId)
                .orElseThrow(() -> new IllegalArgumentException("Meni ne postoji."));

        Restoran restoran = meni.getRestoran();
        if (restoran == null || !restoran.getMenadzer().getKorisnikId().equals(trenutniKorisnikId)) {
            throw new SecurityException("Nemate ovlašćenje da dodajete stavke u ovaj meni!");
        }

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


        StavkaMenija stavkaMenija = StavkaMenija.builder()
                .meni(meni)
                .proizvod(proizvod)
                .vremePripremeMin(request.getVremePripremeMin())
                .vremePripremeMax(request.getVremePripremeMax())
                .cena(request.getCena())
                .dostupno(true)
                .obrisan(false)
                .build();
        stavkaMenijaRepository.save(stavkaMenija);
    }

}