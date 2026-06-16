package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.MeniProizvodiDTO;
import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.ProizvodRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProizvodService {

    private final ProizvodRepository proizvodRepository;

    @Autowired
    private StavkaMenijaRepository stavkaMenijaRepository;

    @Autowired
    private MeniRepository meniRepository;

    public List<Proizvod> getAll() { return proizvodRepository.findAll(); }

    public Proizvod getById(Long id) {
        return proizvodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proizvod nije pronadjen: " + id));
    }

    public List<Proizvod> getByKategorija(Long id) {
        return proizvodRepository.findByKategorija_KategorijaId(id);
    }

    public List<Proizvod> search(String naziv) {
        return proizvodRepository.findByNazivContainingIgnoreCase(naziv);
    }

    public Proizvod create(Proizvod proizvod) { return proizvodRepository.save(proizvod); }

    public Proizvod update(Long id, Proizvod updated) {
        Proizvod existing = getById(id);
        existing.setNaziv(updated.getNaziv());
        existing.setOpis(updated.getOpis());
        existing.setKalorije(updated.getKalorije());
        existing.setFotografija(updated.getFotografija());
        existing.setKategorija(updated.getKategorija());
        return proizvodRepository.save(existing);
    }

    public void delete(Long id) { proizvodRepository.deleteById(id); }

    // Kupac - svi proizvodi iz aktivnih menija restorana (uz vremenski filter)
    // uz informacije  o meniju
    public List<MeniProizvodiDTO> getProizvodiZaKupca(Long restoranId) {
        //return proizvodRepository.findProizvodiIzAktivnihMenija(restoranId, LocalTime.now());

        // pronalazimo stavke menija koje su aktivne za restoran
        /*List<StavkaMenija> aktivneStavke = stavkaMenijaRepository.findAktivneStavkeZaRestoran(restoranId);

        // uzimamo cenu svakog proizvoda iz stavki menija
        return aktivneStavke.stream().map(stavka -> {
            Proizvod p = stavka.getProizvod();
            p.setCena(stavka.getCena());
            return p;
        }).toList();   */

        List<Meni> aktivniMeniji = meniRepository.findByRestoranRestoranIdAndAktivanTrue(restoranId);
        List<StavkaMenija> aktivneStavkeRestorana = stavkaMenijaRepository.findAktivneStavkeZaRestoran(restoranId);

        // grupisi stavke po meniId
        Map<Long, List<StavkaMenija>> stavkePoMeniju = aktivneStavkeRestorana.stream()
                .collect(Collectors.groupingBy(s -> s.getMeni().getMeniId()));

        return aktivniMeniji.stream().map(meni -> {
            MeniProizvodiDTO dto = new MeniProizvodiDTO();
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

            List<Proizvod> proizvodi = stavkeZaOvajMeni.stream().map(stavka -> {
                Proizvod p = stavka.getProizvod();
                p.setCena(stavka.getCena());
                return p;
            }).toList();

            dto.setProizvodi(proizvodi);
            return dto;
        }).toList();

    }

    // Kupac - pretraga po nazivu unutar aktivnih menija restorana (uz vremenski filter)
    public List<Proizvod> searchProizvodiZaKupca(Long restoranId, String naziv) {
        return proizvodRepository.searchProizvodiIzAktivnihMenija(restoranId, naziv, LocalTime.now());
    }

    // Kupac - filtriranje po kategoriji unutar aktivnih menija restorana (uz vremenski filter)
    public List<Proizvod> getProizvodiZaKupcaByKategorija(Long restoranId, Long kategorijaId) {
        return proizvodRepository.findProizvodiIzAktivnihMenijaByKategorija(restoranId, kategorijaId, LocalTime.now());
    }
}