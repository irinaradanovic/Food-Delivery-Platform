package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.MeniUpdateDTO;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.SezonskiMeni;
import fooddelivery.food_delivery_platform.model.VremenskiMeni;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeniService {

    @Autowired
    private MeniRepository meniRepository;

    public List<Meni> getAll() { return meniRepository.findAll(); }

    public List<Meni> findByRestoranRestoranId(Long restoranId) {
        return meniRepository.findByRestoranRestoranId(restoranId);
    }

    public Meni getById(Long id) {
        return meniRepository.findById(id).orElse(null);
    }

    @Transactional
    public Meni updateMenu(Long id, MeniUpdateDTO dto) {
        // Validacija postojanja menija
        Meni meni = meniRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meni sa ID-jem " + id + " ne postoji u bazi."));

        // Ažuriranje zajedničkih (osnovnih) informacija
        meni.setNaziv(dto.getNaziv());
        meni.setOpis(dto.getOpis());

        // Polimorfno ažuriranje na osnovu konkretne podklase
        if (meni instanceof SezonskiMeni) {
            SezonskiMeni sezonski = (SezonskiMeni) meni;
            sezonski.setPocetakSezone(dto.getPocetakSezone());
            sezonski.setKrajSezone(dto.getKrajSezone());
        }
        else if (meni instanceof VremenskiMeni) {
            VremenskiMeni vremenski = (VremenskiMeni) meni;
            vremenski.setVremeOd(dto.getVremeOd());
            vremenski.setVremeDo(dto.getVremeDo());
        }
        // Ako je StandardniMeni, promeniće se samo naziv i opis (iznad)

        // Čuvanje promena u bazi
        return meniRepository.save(meni);
    }
}
