package fooddelivery.food_delivery_platform.service;

import fooddelivery.food_delivery_platform.model.Porudzbina;
import fooddelivery.food_delivery_platform.model.Proizvod;
import fooddelivery.food_delivery_platform.model.Racun;
import fooddelivery.food_delivery_platform.model.RacunStavka;
import fooddelivery.food_delivery_platform.model.Restoran;
import fooddelivery.food_delivery_platform.model.StatusRacuna;
import fooddelivery.food_delivery_platform.model.StavkaMenija;
import fooddelivery.food_delivery_platform.model.StavkaPorudzbine;
import fooddelivery.food_delivery_platform.model.TipRacuna;
import fooddelivery.food_delivery_platform.repository.RacunRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RacunService {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

    private final RacunRepository racunRepository;

    @Transactional
    public Racun izdajRacunAkoNePostoji(Porudzbina porudzbina, Long korisnikId) {
        return racunRepository.findByPorudzbinaPorudzbinaIdAndTip(porudzbina.getPorudzbinaId(), TipRacuna.RACUN)
                .orElseGet(() -> {
                    Racun racun = napraviRacun(porudzbina, TipRacuna.RACUN, null, korisnikId);
                    Racun sacuvan = racunRepository.save(racun);
                    sacuvan.setBrojRacuna(formatBrojRacuna("R", sacuvan.getRacunId()));
                    return racunRepository.save(sacuvan);
                });
    }

    @Transactional
    public Racun stornirajAkoPostoji(Porudzbina porudzbina, Long korisnikId) {
        Racun original = racunRepository
                .findByPorudzbinaPorudzbinaIdAndTip(porudzbina.getPorudzbinaId(), TipRacuna.RACUN)
                .orElse(null);
        if (original == null) {
            return null;
        }
        return racunRepository.findByPorudzbinaPorudzbinaIdAndTip(porudzbina.getPorudzbinaId(), TipRacuna.STORNO)
                .orElseGet(() -> {
                    original.setStatus(StatusRacuna.STORNIRAN);
                    racunRepository.save(original);

                    Racun storno = napraviRacun(porudzbina, TipRacuna.STORNO, original, korisnikId);
                    Racun sacuvan = racunRepository.save(storno);
                    sacuvan.setBrojRacuna(formatBrojRacuna("S", sacuvan.getRacunId()));
                    return racunRepository.save(sacuvan);
                });
    }

    @Transactional(readOnly = true)
    public boolean postojiRacun(Long porudzbinaId) {
        return racunRepository.existsByPorudzbinaPorudzbinaIdAndTip(porudzbinaId, TipRacuna.RACUN);
    }

    @Transactional(readOnly = true)
    public boolean postojiStorno(Long porudzbinaId) {
        return racunRepository.existsByPorudzbinaPorudzbinaIdAndTip(porudzbinaId, TipRacuna.STORNO);
    }

    @Transactional(readOnly = true)
    public byte[] generisiPdf(Long porudzbinaId, TipRacuna tip) {
        Racun racun = racunRepository.findByPorudzbinaPorudzbinaIdAndTip(porudzbinaId, tip)
                .orElseThrow(() -> new RuntimeException("Racun nije pronadjen za porudzbinu: " + porudzbinaId));
        try {
            return renderPdf(racun);
        } catch (IOException e) {
            throw new RuntimeException("Generisanje PDF racuna nije uspelo.", e);
        }
    }

    private Racun napraviRacun(Porudzbina porudzbina, TipRacuna tip, Racun original, Long korisnikId) {
        Racun racun = Racun.builder()
                .porudzbina(porudzbina)
                .tip(tip)
                .status(StatusRacuna.IZDAT)
                .originalniRacun(original)
                .datumIzdavanja(LocalDateTime.now())
                .izdaoKorisnikId(korisnikId)
                .cenaArtikala(scale(porudzbina.getCenaArtikala()))
                .cenaDostave(scale(porudzbina.getCenaDostave()))
                .popustArtikli(scale(porudzbina.getPopustArtikli()))
                .popustDostava(scale(porudzbina.getPopustDostava()))
                .ukupnaCena(scale(porudzbina.getUkupnaCena()))
                .nacinPlacanja(porudzbina.getNacinPlacanja())
                .build();

        for (StavkaPorudzbine stavka : porudzbina.getStavke()) {
            StavkaMenija stavkaMenija = stavka.getStavkaMenija();
            Proizvod proizvod = stavkaMenija != null ? stavkaMenija.getProizvod() : null;
            RacunStavka racunStavka = RacunStavka.builder()
                    .racun(racun)
                    .stavkaMenijaId(stavkaMenija != null ? stavkaMenija.getStavkaId() : null)
                    .proizvodId(proizvod != null ? proizvod.getProizvodId() : null)
                    .naziv(proizvod != null ? proizvod.getNaziv() : "Stavka menija")
                    .kolicina(stavka.getKolicina())
                    .jedinicnaCena(scale(stavkaMenija != null ? stavkaMenija.getCena() : BigDecimal.ZERO))
                    .ukupnaCena(scale(stavka.getCena()))
                    .napomena(stavka.getNapomena())
                    .build();
            racun.getStavke().add(racunStavka);
        }
        return racun;
    }

    private byte[] renderPdf(Racun racun) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = 790;
                write(content, 50, y, 18, true, racun.getTip() == TipRacuna.STORNO ? "STORNO RACUN" : "RACUN");
                y -= 28;
                write(content, 50, y, 11, false, "Broj: " );
                y -= 16;
                write(content, 50, y, 11, false, "Datum izdavanja: " + formatDate(racun.getDatumIzdavanja()));
                y -= 16;
                write(content, 50, y, 11, false, "Porudzbina: #" + racun.getPorudzbina().getPorudzbinaId());
                y -= 16;
                write(content, 50, y, 11, false, "Restoran: " + restoranNaziv(racun.getPorudzbina()));
                y -= 16;
                write(content, 50, y, 11, false, "Kupac: " + kupacNaziv(racun.getPorudzbina()));
                y -= 16;
                write(content, 50, y, 11, false, "Adresa dostave: " );
                y -= 16;
                write(content, 50, y, 11, false, "Nacin placanja: " );
                if (racun.getTip() == TipRacuna.STORNO) {
                    y -= 16;
                    write(content, 50, y, 11, false, "Originalni racun: " );
                }

                y -= 34;
                write(content, 50, y, 12, true, "Stavke");
                y -= 18;
                write(content, 50, y, 10, true, "Naziv");
                write(content, 300, y, 10, true, "Kol.");
                write(content, 350, y, 10, true, "Cena");
                write(content, 450, y, 10, true, "Ukupno");
                y -= 12;
                line(content, 50, y, 545, y);
                y -= 16;

                for (RacunStavka stavka : racun.getStavke()) {
                    write(content, 50, y, 10, false, stavka.getNaziv());
                    write(content, 300, y, 10, false, String.valueOf(stavka.getKolicina()));
                    write(content, 350, y, 10, false, rsd(stavka.getJedinicnaCena()));
                    write(content, 450, y, 10, false, rsd(stavka.getUkupnaCena()));
                    y -= 14;
                    if (stavka.getNapomena() != null && !stavka.getNapomena().isBlank()) {
                        write(content, 66, y, 9, false, "Napomena: " + stavka.getNapomena());
                        y -= 12;
                    }
                    if (y < 120) {
                        write(content, 50, y, 10, false, "Nastavak na sledecoj strani nije podrzan za ovaj prikaz.");
                        break;
                    }
                }

                y -= 10;
                line(content, 330, y, 545, y);
                y -= 18;
                writeSummary(content, y, "Artikli", racun.getCenaArtikala());
                y -= 16;
                writeSummary(content, y, "Dostava", racun.getCenaDostave());
                y -= 16;
                writeSummary(content, y, "Popust", scale(racun.getPopustArtikli()).add(scale(racun.getPopustDostava())));
                y -= 18;
                write(content, 350, y, 12, true, "Ukupno:");
                write(content, 450, y, 12, true, rsd(racun.getUkupnaCena()));

            }

            document.save(out);
            return out.toByteArray();
        }
    }

    private void writeSummary(PDPageContentStream content, float y, String label, BigDecimal value) throws IOException {
        write(content, 350, y, 10, false, label + ":");
        write(content, 450, y, 10, false, rsd(value));
    }

    private void write(PDPageContentStream content, float x, float y, int size, boolean bold, Object value) throws IOException {
        content.beginText();
        content.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, size);
        content.newLineAtOffset(x, y);
        content.showText((String) value);
        content.endText();
    }

    private void line(PDPageContentStream content, float startX, float startY, float endX, float endY) throws IOException {
        content.moveTo(startX, startY);
        content.lineTo(endX, endY);
        content.stroke();
    }

    private String restoranNaziv(Porudzbina porudzbina) {
        return porudzbina.getStavke().stream()
                .map(StavkaPorudzbine::getStavkaMenija)
                .map(StavkaMenija::getMeni)
                .map(meni -> meni != null ? meni.getRestoran() : null)
                .map(Restoran::getNaziv)
                .filter(naziv -> naziv != null && !naziv.isBlank())
                .findFirst()
                .orElse("Restoran");
    }

    private String kupacNaziv(Porudzbina porudzbina) {
        if (porudzbina.getKupac() == null) return "-";
        return porudzbina.getKupac().getIme() + " " + porudzbina.getKupac().getPrezime();
    }

    private String formatBrojRacuna(String prefix, Long id) {
        return prefix + "-" + LocalDateTime.now().getYear() + "-" + String.format("%06d", id);
    }

    private String formatDate(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME);
    }

    private String rsd(BigDecimal value) {
        return scale(value).toPlainString() + " RSD";
    }

    private BigDecimal scale(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }



}
