package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.MeniUpdateDTO;
import fooddelivery.food_delivery_platform.model.Meni;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.model.SezonskiMeni;
import fooddelivery.food_delivery_platform.model.VremenskiMeni;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
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

    public List<Meni> getAll() { return meniRepository.findAll(); }

    public List<Meni> findByRestoranRestoranId(Long restoranId) {
        return meniRepository.findByRestoranRestoranId(restoranId);
    }

    public Meni getById(Long id) {
        return meniRepository.findById(id).orElse(null);
    }

    @Transactional
    public Meni updateMenu(Long id, MeniUpdateDTO dto, Long trenutniKorisnikId) {
        Meni meni = meniRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meni sa ID-jem " + id + " ne postoji u bazi."));

        // sigurnosna validacija
        Restoran restoran = meni.getRestoran();
        if (restoran == null || !restoran.getMenadzer().getKorisnikId().equals(trenutniKorisnikId)) {
            throw new AccessDeniedException("Nemate ovlašćenje da menjate meni ovog restorana!");
        }
        // azuriranje osnovnih informacija
        meni.setNaziv(dto.getNaziv());
        meni.setOpis(dto.getOpis());

        // sezonski meni moze da menja datume
        if (meni instanceof SezonskiMeni) {
            SezonskiMeni sezonski = (SezonskiMeni) meni;
            sezonski.setPocetakSezone(dto.getPocetakSezone());
            sezonski.setKrajSezone(dto.getKrajSezone());
        }
        // za vremenski se moze promeniti sastnica
        else if (meni instanceof VremenskiMeni) {
            VremenskiMeni vremenski = (VremenskiMeni) meni;
            vremenski.setVremeOd(dto.getVremeOd());
            vremenski.setVremeDo(dto.getVremeDo());
        }

        return meniRepository.save(meni);
    }


    @Transactional
    public void deactivateMenu(Long id, Long trenutniKorisnikId) {
        Meni meni = meniRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meni sa ID-jem " + id + " nije pronađen."));

        Restoran restoran = meni.getRestoran();
        if (restoran == null || !restoran.getMenadzer().getKorisnikId().equals(trenutniKorisnikId)) {
            throw new AccessDeniedException("Nemate ovlašćenje da menjate meni ovog restorana!");
        }
        meni.setAktivan(false);
        meni.setDatumDo(LocalDate.now()); // Postavlja se datum kada je meni deaktiviran

        meniRepository.save(meni);
    }
}
