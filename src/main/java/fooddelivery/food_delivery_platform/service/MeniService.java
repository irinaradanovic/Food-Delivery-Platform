package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.MeniUpdateDTO;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.model.SezonskiMeni;
import fooddelivery.food_delivery_platform.model.VremenskiMeni;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.RestoranRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeniService {

    @Autowired
    private MeniRepository meniRepository;

    @Autowired
    private RestoranRepository restoranRepository;

    public List<Meni> getAll() { return meniRepository.findAll(); }

    public List<Meni> findByRestoranRestoranId(Long restoranId) {
        return meniRepository.findByRestoranRestoranId(restoranId);
    }

    public List<Meni> findAktivniByRestoranRestoranId(Long restoranId) {
        return meniRepository.findByRestoranRestoranIdAndAktivanTrue(restoranId);
    }

    public Meni getById(Long id) {
        return meniRepository.findById(id).orElse(null);
    }

    @Transactional
    public Meni updateMenu(Long id, MeniUpdateDTO dto, Long trenutniKorisnikId) {
        Meni meni = meniRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meni sa ID-jem " + id + " ne postoji u bazi."));

        Restoran restoran = meni.getRestoran();
        if (restoran == null || !restoran.getMenadzer().getKorisnikId().equals(trenutniKorisnikId)) {
            throw new AccessDeniedException("Nemate ovlascenje da menjate meni ovog restorana!");
        }

        meni.setNaziv(dto.getNaziv());
        meni.setOpis(dto.getOpis());

        if (meni instanceof SezonskiMeni) {
            SezonskiMeni sezonski = (SezonskiMeni) meni;
            sezonski.setPocetakSezone(dto.getPocetakSezone());
            sezonski.setKrajSezone(dto.getKrajSezone());
        } else if (meni instanceof VremenskiMeni) {
            VremenskiMeni vremenski = (VremenskiMeni) meni;
            vremenski.setVremeOd(dto.getVremeOd());
            vremenski.setVremeDo(dto.getVremeDo());
        }

        return meniRepository.save(meni);
    }

    @Transactional
    public void deactivateMenu(Long id, Long trenutniKorisnikId) {
        Meni meni = meniRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meni sa ID-jem " + id + " nije pronadjen."));

        Restoran restoran = meni.getRestoran();
        if (restoran == null || !restoran.getMenadzer().getKorisnikId().equals(trenutniKorisnikId)) {
            throw new AccessDeniedException("Nemate ovlascenje da menjate meni ovog restorana!");
        }
        meni.setAktivan(false);
        meni.setDatumDo(LocalDate.now());

        meniRepository.save(meni);
    }

    @Transactional
    public Meni createMenu(Meni noviMeni, Long trenutniKorisnikId) {
        if (noviMeni.getRestoran() == null || noviMeni.getRestoran().getRestoranId() == null) {
            throw new IllegalArgumentException("Restoran mora biti prosleden u zahtevu.");
        }

        Long restoranId = noviMeni.getRestoran().getRestoranId();

        Restoran restoran = restoranRepository.findById(restoranId)
                .orElseThrow(() -> new EntityNotFoundException("Restoran sa ID-jem " + restoranId + " ne postoji."));

        if (restoran.getMenadzer() == null || !restoran.getMenadzer().getKorisnikId().equals(trenutniKorisnikId)) {
            throw new AccessDeniedException("Nemate ovlascenje da kreirate meni za ovaj restoran jer niste njegov menadzer!");
        }
        noviMeni.setRestoran(restoran);
        noviMeni.setVerzija("v1");
        noviMeni.setAktivan(true);

        return meniRepository.save(noviMeni);
    }
}