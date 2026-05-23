package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.repository.ProizvodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProizvodService {

    private final ProizvodRepository proizvodRepository;

    public List<Proizvod> getAll() { return proizvodRepository.findAll(); }

    public Proizvod getById(Long id) {
        return proizvodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proizvod nije pronađen: " + id));
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
}