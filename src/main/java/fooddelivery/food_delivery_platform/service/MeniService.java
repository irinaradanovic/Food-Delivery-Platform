package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.dto.MeniUpdateDTO;
import fooddelivery.food_delivery_platform.model.*;
import fooddelivery.food_delivery_platform.repository.MeniRepository;
import fooddelivery.food_delivery_platform.repository.RestoranRepository;
import fooddelivery.food_delivery_platform.repository.StavkaMenijaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeniService {

    @Autowired
    private MeniRepository meniRepository;

    @Autowired
    private RestoranRepository restoranRepository;

    @Autowired
    private StavkaMenijaRepository stavkaMenijaRepository;


    public List<Meni> getAll() { return meniRepository.findAll(); }

    public List<Meni> findByRestoranRestoranId(Long restoranId) {
        return meniRepository.findJedinstveniMenijiPoGrupama(restoranId);
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

        checkAccess(meni, trenutniKorisnikId);

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

        checkAccess(meni, trenutniKorisnikId);
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

        meniRepository.save(noviMeni);
        noviMeni.setGrupniMeniId(noviMeni.getMeniId());
        return meniRepository.save(noviMeni);
    }


    public Meni cloneMenu(Meni original) {
        if (original instanceof SezonskiMeni s) {
            SezonskiMeni novi = new SezonskiMeni();
            copyBasicMenuInfo(s, novi);
            novi.setPocetakSezone(s.getPocetakSezone());
            novi.setKrajSezone(s.getKrajSezone());
            return novi;
        } else if (original instanceof VremenskiMeni v) {
            VremenskiMeni novi = new VremenskiMeni();
            copyBasicMenuInfo(v, novi);
            novi.setVremeOd(v.getVremeOd());
            novi.setVremeDo(v.getVremeDo());
            return novi;
        } else {
            StandardniMeni novi = new StandardniMeni();
            copyBasicMenuInfo(original, novi);
            return novi;
        }
    }

    private void copyBasicMenuInfo(Meni original, Meni novi) {
        novi.setNaziv(original.getNaziv());
        novi.setOpis(original.getOpis());
        novi.setRestoran(original.getRestoran());
        novi.setGrupniMeniId(original.getGrupniMeniId());
    }

   public void copyMenuItems(Long staroMeniId, Meni noviMeni) {
        List<StavkaMenija> stareStavke = stavkaMenijaRepository
                .findByMeniMeniIdAndObrisanFalse(staroMeniId);
        for (StavkaMenija stara : stareStavke) {
            StavkaMenija nova = StavkaMenija.builder()
                    .meni(noviMeni)
                    .proizvod(stara.getProizvod())
                    .vremePripremeMin(stara.getVremePripremeMin())
                    .vremePripremeMax(stara.getVremePripremeMax())
                    .cena(stara.getCena())
                    .dostupno(stara.isDostupno())
                    .obrisan(false)
                    .build();
            stavkaMenijaRepository.save(nova);
        }
    }

    public String findNextVersion(Long grupniMeniId) {
        Integer maxVerzija = meniRepository.findMaxVersionNumberByGrupniId(grupniMeniId);

        int sledeciBroj = (maxVerzija == null) ? 1 : maxVerzija + 1;

        return "v" + sledeciBroj;
    }

    private void checkAccess(Meni meni, Long korisnikId) {
        Restoran restoran = meni.getRestoran();
        if (restoran == null || !restoran.getMenadzer().getKorisnikId().equals(korisnikId)) {
            throw new AccessDeniedException("Nemate ovlašćenje da menjate ovaj meni!");
        }
    }

    public List<Meni> getMenuVersionHstory(Long grupniMeniId){
        return meniRepository.findByGrupniMeniIdOrderByVerzijaDesc(grupniMeniId);
    }

    @Transactional
    public Meni rollbackToVersion(Long meniId, Long userId) {
        Meni staraVerzija = meniRepository.findById(meniId)
                .orElseThrow(() -> new EntityNotFoundException("Meni nije pronađen"));

        checkAccess(staraVerzija, userId);

        // deaktiviraj sve aktivne verzije u grupi
      /*  meniRepository.findByGrupniMeniIdAndAktivanTrue(staraVerzija.getGrupniMeniId())
                .ifPresent(trenutnoAktivni -> {
                    trenutnoAktivni.setAktivan(false);
                    trenutnoAktivni.setDatumDo(LocalDate.now());
                    meniRepository.save(trenutnoAktivni);
                }); */
        //kopiraj staru verziju
        Meni novaVerzija = cloneMenu(staraVerzija);

        novaVerzija.setAktivan(true);
        novaVerzija.setDatumOd(LocalDate.now());
        novaVerzija.setDatumDo(null);

        String sledecaVerzija = findNextVersion(staraVerzija.getGrupniMeniId());
        novaVerzija.setVerzija(sledecaVerzija);

        meniRepository.save(novaVerzija);
        meniRepository.flush();

        copyMenuItems(meniId, novaVerzija);
        return novaVerzija;
    }
}