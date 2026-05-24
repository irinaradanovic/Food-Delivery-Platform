package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Kupac;
import fooddelivery.food_delivery_platform.repository.KupacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KupacService {

    private final KupacRepository kupacRepository;

    public List<Kupac> getAll() {
        return kupacRepository.findAll();
    }

    public Kupac getById(Long id) {
        return kupacRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kupac nije pronađen: " + id));
    }
    public List<Kupac> findByEmail(String email) {
        return kupacRepository.findByEmail(email).map(List::of).orElse(List.of());
    }

    public Kupac create(Kupac kupac) {
        if (kupacRepository.existsByEmail(kupac.getEmail())) {
            throw new RuntimeException("Email je već u upotrebi: " + kupac.getEmail());
        }
        return kupacRepository.save(kupac);
    }

    public Kupac update(Long id, Kupac updated) {
        Kupac existing = getById(id);
        existing.setIme(updated.getIme());
        existing.setPrezime(updated.getPrezime());
        existing.setEmail(updated.getEmail());
        existing.setTelefon(updated.getTelefon());
        existing.setAdresa(updated.getAdresa());
        return kupacRepository.save(existing);
    }

    public void delete(Long id) {
        kupacRepository.deleteById(id);
    }
}