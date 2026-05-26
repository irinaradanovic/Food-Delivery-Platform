-- Menadzeri
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES
                                                                                                 (1, 'Marko', 'Marković', '064123456', 'password123', 'menadzer1@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (2, 'Nikola', 'Nikolić', '065987654', 'password123', 'menadzer2@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (3, 'Jovana', 'Jovanić', '063111222', 'password123', 'menadzer3@test.com', '2026-05-25', 'MENADZER');

INSERT INTO menadzeri (korisnik_id) VALUES (1), (2), (3);

-- Restorani
INSERT INTO restorani (restoran_id, naziv, adresa, kontakt, menadzer_id) VALUES
                                                                             (1, 'Big Bite Centar', 'Trg Slobode 4, Novi Sad', '021-555-333', 1),
                                                                             (2, 'Pizzeria Napoli', 'Bulevar Oslobodjenja 12, Novi Sad', '021-444-555', 2),
                                                                             (3, 'Green Garden Healthy', 'Fruškogorska 8, Novi Sad', '021-666-777', 3),
                                                                             (4, 'Fun!', 'Jevrejska 3, Novi Sad', '021-888-777', 1);

-- Kategorije
INSERT INTO kategorije (kategorija_id, naziv) VALUES
                                                  (1, 'Burgers'),
                                                  (2, 'Pizzas'),
                                                  (3, 'Salads'),
                                                  (4, 'Drinks'),
                                                  (5, 'Deserts'),
                                                  (6, 'Chicken');

-- Alergeni
INSERT INTO alergeni (alergen_id, naziv) VALUES
                                             (1, 'Gluten'),
                                             (2, 'Kikiriki'),
                                             (3, 'Laktoza'),
                                             (4, 'Jaja'),
                                             (5, 'Pšenica');

-- Sastojci
INSERT INTO sastojci (sastojak_id, naziv) VALUES
                                              (1, 'Govedina'),
                                              (2, 'Mocarela'),
                                              (3, 'Pelat'),
                                              (4, 'Cezar sos'),
                                              (5, 'Zelena salata'),
                                              (6, 'Čedar sir'),
                                              (7, 'Piletina'),
                                              (8, 'Tjestenina'),
                                              (9, 'Avokado'),
                                              (10, 'Jaja za omlet'),
                                              (11, 'Čokolada');

-- Proizvodi
INSERT INTO proizvodi (proizvod_id, naziv, opis, kalorije, cena, fotografija, kolicina, merna_jedinica, kategorija_id) VALUES
-- Burgers (Kat 1)
(1, 'Classic Burger', 'Sočna govedina sa čedar sirom', 650, 550.00, 'images/food/burger1.jpg', 350, 'g', 1),
(2, 'Monster Burger', 'Dupla govedina, hrskava slanina i BBQ sos', 950, 820.00, 'images/food/burger2.jpg', 500, 'g', 1),
-- Pizzas (Kat 2)
(3, 'Pizza Capricciosa', 'Klasična pica sa šunkom, sirom i šampinjonima', 800, 780.00, 'images/food/capriciossa.jpg', 500, 'g', 2),
(4, 'Pizza Marinara', 'Pelat, bijeli luk, maslinovo ulje i bosiljak', 600, 620.00, 'images/food/marinara.jpg', 450, 'g', 2),
(5, 'Pizza Puttanesca', 'Inćuni, kapar, masline i pelat', 750, 790.00, 'images/food/puttanesca.jpg', 480, 'g', 2),
-- Salads (Kat 3)
(6, 'Cezar Salata', 'Sveža salata sa piletinom i krutonima', 450, 490.00, 'images/food/salad1.jpg', 400, 'g', 3),
(7, 'Avocado Mediteran Salata', 'Miks zelenih salata sa avokadom i dresingom', 350, 520.00, 'images/food/salad2.jpg', 350, 'g', 3),
-- Drinks (Kat 4)
(8, 'Coca-Cola', 'Osvježavajuće gazirano piće', 140, 180.00, 'images/food/coca-cola.jpg', 330, 'ml', 4),
(9, 'Domaća Kafa', 'Tradicionalna crna kafa', 20, 130.00, 'images/food/coffee.jpg', 150, 'ml', 4),
-- Deserts (Kat 5)
(10, 'Tiramisu', 'Klasični italijanski kolač sa kafom i maskarponeom', 420, 360.00, 'images/food/tiramisu.jpg', 180, 'g', 5),
(11, 'Cheesecake', 'Lagana podloga od keksa sa krem sirom i voćem', 390, 380.00, 'images/food/cheesecake.jpg', 200, 'g', 5),
-- Chicken (Kat 6)
(12, 'Buffalo Wings', 'Ljuta pileća krilca sa plavim sirom', 700, 640.00, 'images/food/buffalo-wings.jpg', 400, 'g', 6),
(13, 'Hrskava Piletina', 'Panirani pileći file sa pomfritom', 680, 590.00, 'images/food/chicken.jpg', 380, 'g', 6),
-- Ostalo
(14, 'Omelette Klasik', 'Tri jaja, sir, šunka i prilog', 510, 320.00, 'images/food/omelette.jpg', 300, 'g', 3),
(15, 'Avocado Toast', 'Tostirani hleb sa gnječenim avokadom i jajima', 400, 410.00, 'images/food/avocado-toast.jpg', 250, 'g', 3),
(16, 'White Pasta', 'Kremasta tjestenina sa piletinom i bjelim sosom', 850, 690.00, 'images/food/white-pasta.jpg', 400, 'g', 2);

-- Povezivanje Proizvoda sa Sastojcima
INSERT INTO proizvod_sastojci (proizvod_id, sastojak_id) VALUES
                                                             (1, 1), (1, 6),
                                                             (2, 1), (2, 6),
                                                             (3, 2), (3, 3),
                                                             (4, 3),
                                                             (5, 3),
                                                             (6, 4), (6, 5), (6, 7),
                                                             (7, 5), (7, 9),
                                                             (12, 7),
                                                             (13, 7),
                                                             (14, 10),
                                                             (15, 9), (15, 10),
                                                             (16, 7), (16, 8);

-- Povezivanje Proizvoda sa Alergenima
INSERT INTO proizvod_alergeni (proizvod_id, alergen_id) VALUES
                                                            (1, 1), (1, 3),
                                                            (2, 1), (2, 3),
                                                            (3, 1), (3, 3),
                                                            (4, 1),
                                                            (5, 1),
                                                            (6, 4),
                                                            (10, 1), (10, 3), (10, 4),
                                                            (11, 1), (11, 3),
                                                            (13, 1), (13, 4),
                                                            (14, 4),
                                                            (15, 1), (15, 4),
                                                            (16, 1), (16, 3);

-- Meniji
-- Restoran 1: STANDARDNI
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, restoran_id) VALUES
    (1, 'Glavni Meni Big Bite', 'Standardna ponuda hrane i pića', 'STANDARDNI', 'v1', true, '2026-05-26', 1);

-- Restoran 2: VREMENSKI (Doručak od 08:00 do 11:00)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, vreme_od, vreme_do, restoran_id) VALUES
    (2, 'Jutarnji Meni Napoli', 'Najbolji doručak u gradu', 'VREMENSKI', 'v1', true, '2026-05-26', '08:00:00', '11:00:00', 2);

-- Restoran 3: SEZONSKI (Letnji meni)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, pocetak_sezone, kraj_sezone, restoran_id) VALUES
    (3, 'Letnji Meni 2026 Green', 'Laka letnja osveženja i obroci', 'SEZONSKI', 'v1', true, '2026-05-26', '2026-06-01', '2026-08-31', 3);


-- Povezivanje Menija i Proizvoda
INSERT INTO stavke_menija (stavka_id, meni_id, proizvod_id, cena, dostupno, vreme_pripreme_min, vreme_pripreme_max, obrisan) VALUES
-- Stavke za Meni 1
(1, 1, 1, 550.00, true, 10, 15, false),
(2, 1, 2, 820.00, true, 12, 18, false),
(3, 1, 12, 640.00, true, 15, 20, false),
(4, 1, 8, 180.00, true, 2, 5, false),

-- Stavke za Meni 2
(5, 2, 14, 320.00, true, 8, 12, false),
(6, 2, 15, 410.00, true, 5, 10, false),
(7, 2, 3, 750.00, true, 12, 20, false),
(8, 2, 4, 620.00, true, 10, 15, false),
(9, 2, 9, 130.00, true, 3, 5, false),
(10, 2, 10, 360.00, true, 2, 4, false),

-- Stavke za Meni 3
(11, 3, 6, 490.00, true, 5, 10, false),
(12, 3, 7, 520.00, true, 5, 8, false),
(13, 3, 16, 690.00, true, 15, 22, false),
(14, 3, 11, 380.00, true, 2, 5, false);

-- Sinhronizacija sekvenci za bazu
SELECT setval(pg_get_serial_sequence('korisnici', 'korisnik_id'), MAX(korisnik_id)) FROM korisnici;
SELECT setval(pg_get_serial_sequence('kategorije', 'kategorija_id'), MAX(kategorija_id)) FROM kategorije;
SELECT setval(pg_get_serial_sequence('alergeni', 'alergen_id'), MAX(alergen_id)) FROM alergeni;
SELECT setval(pg_get_serial_sequence('sastojci', 'sastojak_id'), MAX(sastojak_id)) FROM sastojci;
SELECT setval(pg_get_serial_sequence('proizvodi', 'proizvod_id'), MAX(proizvod_id)) FROM proizvodi;
SELECT setval(pg_get_serial_sequence('meniji', 'meni_id'), MAX(meni_id)) FROM meniji;
SELECT setval(pg_get_serial_sequence('stavke_menija', 'stavka_id'), MAX(stavka_id)) FROM stavke_menija;