ALTER TABLE stavke_menija
    ADD CONSTRAINT uq_meni_proizvod UNIQUE (meni_id, proizvod_id);
-- Menadzeri
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES
    (99, 'Admin', 'Admin', '060000000', 'admin123', 'admin@bigbite.com', '2026-05-25', 'ADMIN');

INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES
                                                                                                 (1, 'Marko', 'Marković', '064123456', 'password123', 'menadzer1@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (2, 'Nikola', 'Nikolić', '065987654', 'password123', 'menadzer2@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (3, 'Jovana', 'Jovanić', '063111222', 'password123', 'menadzer3@test.com', '2026-05-25', 'MENADZER');

INSERT INTO menadzeri (korisnik_id) VALUES (1), (2), (3);

-- Kupci
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES (4, 'Marko', 'Markovic', '064235768', '123456', 'markovic@gmail.com', '2026-05-25', 'KUPAC');
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES (5, 'Nenad', 'Lalic', '067892568', '123456', 'lalic@gmail.com', '2026-05-25', 'KUPAC');


-- Dostavljaci
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES
                                                                                                 (6, 'Ivana', 'Ivanovic', '064000111', 'password123', 'ivana@test.com', '2026-05-25', 'DOSTAVLJAC'),
                                                                                                 (7, 'Stefan', 'Stavanović', '065222333', 'password123', 'stefan@test.com', '2026-05-25', 'DOSTAVLJAC');

INSERT INTO dostavljaci (korisnik_id, ime, prezime, telefon, trenutna_lat, trenutna_lng, status, prosecna_ocena, broj_dostava) VALUES
                                                                                                                                   (6, 'Ivana', 'Ivanovic', '064000111', 45.2671, 19.8335, 'DOSTUPAN', 4.85, 124),
                                                                                                                                   (7, 'Stefan', 'Stavanovic', '065222333', 45.2551, 19.8452, 'DOSTUPAN', 4.60, 98);

/*INSERT INTO kupci VALUES ('Narodnog fronta 16, Novi Sad', NULL, 4);
INSERT INTO kupci VALUES ('Narodnog fronta 19, Novi Sad', NULL, 5); */




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
                                              (8, 'Testenina'),
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
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, restoran_id, grupni_meni_id) VALUES
    (1, 'Glavni Meni Big Bite', 'Standardna ponuda hrane i pića', 'STANDARDNI', 'v1', true, '2026-05-26', 1, 1);

-- Restoran 2: VREMENSKI (Doručak od 08:00 do 11:00)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, vreme_od, vreme_do, restoran_id, grupni_meni_id) VALUES
    (2, 'Jutarnji Meni Napoli', 'Najbolji doručak u gradu', 'VREMENSKI', 'v1', true, '2026-05-26', '08:00:00', '13:00:00', 2, 2);

-- Restoran 3: SEZONSKI (Letnji meni)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, pocetak_sezone, kraj_sezone, restoran_id, grupni_meni_id) VALUES
    (3, 'Letnji Meni 2026 Green', 'Laka letnja osveženja i obroci', 'SEZONSKI', 'v1', true, '2026-05-26', '2026-06-01', '2026-08-31', 3, 3);

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




-- Kupci (korisnici)

INSERT INTO kupci (korisnik_id, adresa, broj_kartice) VALUES (4, 'Narodnog fronta 16, Novi Sad', NULL);
INSERT INTO kupci (korisnik_id, adresa, broj_kartice) VALUES (5, 'Narodnog fronta 19, Novi Sad', NULL);


-- Porudzbine (korisnik_id: 2->5, porudzbina_id: 1-7 su slobodni)
INSERT INTO porudzbine (porudzbina_id, korisnik_id, adresa_dostave, datum_kreiranja, kupon_id, napomena, status, ukupna_cena)
VALUES
    (1, 5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:08:06.068921', NULL, '',       'KREIRANA', 9.80),
    (2, 5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:08:18.871066', NULL, '2. sprat', 'KREIRANA', 9.80),
    (3, 5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:09:25.310633', NULL, '2. sprat', 'KREIRANA', 9.80),
    (4, 5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:09:32.69165',  NULL, '2. sprat', 'KREIRANA', 9.80),
    (5, 5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:10:02.493091', NULL, '2. sprat', 'KREIRANA', 9.80),
    (6, 5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:10:14.984126', NULL, '2. sprat', 'KREIRANA', 9.80),
    (7, 5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:10:29.991216', NULL, '2. sprat', 'KREIRANA', 9.80);



-- Klikovi (korisnik_id: 5, proizvod_id: mapirani na postojece proizvode 1-16)
-- pid 21->3 (Pizza Capricciosa), pid 25->5 (Pizza Puttanesca), pid 27->4 (Pizza Marinara),
-- pid 30->16 (White Pasta), pid 39->6 (Cezar Salata), pid 47,48->7 (Avocado Med. Salata),
-- pid 44->13 (Hrskava Piletina), pid 42->3 (Pizza Capricciosa), pid 18->10 (Tiramisu),
-- pid 17->16 (White Pasta), pid 29->5 (Pizza Puttanesca), pid 20->7 (Avocado Med. Salata)
INSERT INTO klikovi (klik_id, tip_akcije, vreme_klika, korisnik_id, proizvod_id) VALUES
                                                                                     (1,  'PREGLED',          '2026-05-24 11:18:19.77089',   5, 3),
                                                                                     (2,  'PREGLED',          '2026-05-24 11:18:20.970097',  5, 3),
                                                                                     (3,  'PREGLED',          '2026-05-24 11:18:21.96425',   5, 3),
                                                                                     (4,  'PREGLED',          '2026-05-24 11:18:23.339181',  5, 5),
                                                                                     (5,  'PREGLED',          '2026-05-24 11:18:23.587001',  5, 5),
                                                                                     (6,  'PREGLED',          '2026-05-24 11:20:37.208674',  5, 4),
                                                                                     (7,  'PREGLED',          '2026-05-24 12:21:14.605071',  5, 16),
                                                                                     (8,  'PREGLED',          '2026-05-24 12:21:15.745218',  5, 16),
                                                                                     (9,  'PREGLED',          '2026-05-24 13:09:53.582406',  5, 6),
                                                                                     (10, 'PREGLED',          '2026-05-24 13:13:51.795557',  5, 7),
                                                                                     (11, 'PREGLED',          '2026-05-24 13:13:52.990959',  5, 7),
                                                                                     (12, 'PREGLED',          '2026-05-24 13:15:06.971218',  5, 7),
                                                                                     (13, 'PREGLED',          '2026-05-24 13:19:02.19047',   5, 7),
                                                                                     (14, 'PREGLED',          '2026-05-24 13:19:04.136781',  5, 7),
                                                                                     (15, 'PREGLED',          '2026-05-24 13:20:56.377343',  5, 13),
                                                                                     (16, 'PREGLED',          '2026-05-24 13:21:36.645282',  5, 3),
                                                                                     (17, 'PREGLED',          '2026-05-24 13:22:46.494394',  5, 13),
                                                                                     (18, 'PREGLED',          '2026-05-24 13:46:10.569618',  5, 10),
                                                                                     (19, 'PREGLED',          '2026-05-24 18:22:09.433661',  5, 3),
                                                                                     (20, 'KORPA',            '2026-05-24 18:22:20.19602',   5, 3),
                                                                                     (21, 'KORPA',            '2026-05-24 18:22:31.277277',  5, 3),
                                                                                     (22, 'PREGLED',          '2026-05-24 18:22:49.912077',  5, 16),
                                                                                     (23, 'KORPA',            '2026-05-25 09:07:22.400348',  5, 10),
                                                                                     (24, 'PREGLED',          '2026-05-25 09:12:20.191611',  5, 4),
                                                                                     (25, 'KORPA',            '2026-05-25 09:12:49.583981',  5, 5),
                                                                                     (26, 'KORPA',            '2026-05-25 09:12:53.837157',  5, 5),
                                                                                     (27, 'PREGLED',          '2026-05-25 09:18:08.261056',  5, 16),
                                                                                     (28, 'KORPA',            '2026-05-25 09:45:03.390327',  5, 10),
                                                                                     (29, 'KORPA',            '2026-05-25 09:59:46.08506',   5, 3),
                                                                                     (30, 'KORPA',            '2026-05-25 10:00:06.103191',  5, 10),
                                                                                     (31, 'KORPA',            '2026-05-25 10:17:53.926342',  5, 3),
                                                                                     (32, 'KORPA',            '2026-05-25 10:23:38.663827',  5, 10),
                                                                                     (33, 'PREGLED',          '2026-05-25 10:24:28.563324',  5, 3),
                                                                                     (34, 'KORPA',            '2026-05-25 18:27:16.045984',  5, 10),
                                                                                     (35, 'PLACANJE',         '2026-05-25 18:27:18.820107',  5, 10),
                                                                                     (36, 'KUPOVINA',         '2026-05-25 18:27:18.830747',  5, 10),
                                                                                     (37, 'PREGLED',          '2026-05-25 18:35:28.70401',   5, 16),
                                                                                     (38, 'PREGLED',          '2026-05-25 18:45:38.15342',   5, 10),
                                                                                     (39, 'DODAJ_OMILJENI',   '2026-05-25 23:36:50.74754',   5, 10),
                                                                                     (40, 'UKLONI_OMILJENI',  '2026-05-25 23:36:51.352762',  5, 10),
                                                                                     (41, 'KORPA',            '2026-05-25 23:36:55.221134',  5, 10),
                                                                                     (42, 'UKLONI_IZ_KORPE',  '2026-05-25 23:37:01.971029',  5, 10),
                                                                                     (43, 'REZULTAT_PRETRAGE','2026-05-25 23:38:32.146358',  5, 3),
                                                                                     (44, 'DODAJ_OMILJENI',   '2026-05-25 23:53:18.022442',  5, 7),
                                                                                     (45, 'UKLONI_OMILJENI',  '2026-05-25 23:53:18.753286',  5, 7),
                                                                                     (46, 'DETALJI',          '2026-05-25 23:53:19.440917',  5, 7),
                                                                                     (47, 'KORPA',            '2026-05-25 23:53:24.247347',  5, 7),
                                                                                     (48, 'UKLONI_IZ_KORPE',  '2026-05-25 23:53:27.653239',  5, 7);

-- Pretrage (korisnik_id: stari 1->4, stari 2->5)
INSERT INTO pretrage (pretraga_id, tekst_upita, tip_pretrage, vreme_pretrage, korisnik_id) VALUES
                                                                                               (1,  'pica',       'OPSTA', '2026-05-24 10:43:09.564465', 4),
                                                                                               (2,  'marg',       'OPSTA', '2026-05-24 11:19:54.241524', 5),
                                                                                               (3,  'margherita', 'OPSTA', '2026-05-24 11:20:06.445416', 5),
                                                                                               (4,  'carbonara',  'OPSTA', '2026-05-24 11:20:23.631874', 5),
                                                                                               (5,  'Carbonara',  'OPSTA', '2026-05-24 11:20:28.330843', 5),
                                                                                               (6,  'Carbonara',  'OPSTA', '2026-05-24 11:20:42.420398', 5),
                                                                                               (7,  'carbonara',  'OPSTA', '2026-05-24 11:31:22.584595', 5),
                                                                                               (8,  'carbonara',  'OPSTA', '2026-05-24 11:31:22.584595', 5),
                                                                                               (9,  'margherita', 'OPSTA', '2026-05-24 13:34:29.974048', 5),
                                                                                               (10, 'marg',       'OPSTA', '2026-05-24 13:34:34.080257', 5),
                                                                                               (11, 'car',        'OPSTA', '2026-05-24 13:34:36.56451',  5),
                                                                                               (12, 'limunada',   'OPSTA', '2026-05-25 23:38:32.113215', 5),
                                                                                               (13, 'hygui',      'OPSTA', '2026-05-25 23:39:20.161768', 5);
INSERT INTO stavke_menija (stavka_id, meni_id, proizvod_id, cena, dostupno, vreme_pripreme_min, vreme_pripreme_max, obrisan) VALUES
                                                                                                                                 (17, 2, 17, 850.00, true, 8, 12, false),
                                                                                                                                 (18, 2, 18, 980.00, true, 8, 12, false),
                                                                                                                                 (19, 2, 19, 1050.00, true, 10, 15, false),
                                                                                                                                 (20, 2, 20, 780.00, true, 8, 12, false),
                                                                                                                                 (21, 1, 21, 750.00, true, 10, 15, false),
                                                                                                                                 (22, 1, 22, 950.00, true, 12, 18, false),
                                                                                                                                 (23, 1, 23, 820.00, true, 10, 15, false);

INSERT INTO stavke_porudzbine (stavka_id, cena, porudzbina_id, kolicina, stavka_menija_id) VALUES
                                                                                               (DEFAULT, 550.00, 1, 1, 1),
                                                                                               (DEFAULT, 820.00, 1, 2, 2),
                                                                                               (DEFAULT, 320.00, 2, 1, 5),
                                                                                               (DEFAULT, 410.00, 2, 1, 6),
                                                                                               (DEFAULT, 750.00, 3, 1, 7),
                                                                                               (DEFAULT, 620.00, 3, 1, 8),
                                                                                               (DEFAULT, 130.00, 3, 1, 9),
                                                                                               (DEFAULT, 360.00, 3, 1, 10);

INSERT INTO kuponi (kupon_id, kod, popust_iznos, popust_procenat, vazi_od, vazi_do, aktivan, max_upotreba, upotrebljeno_puta, vlasnik_id) VALUES
                                                                                                                                              (1, 'WELCOME100', 100.00, NULL, '2026-05-01 00:00:00', '2028-12-31 23:59:59', true, 100, 0, 4),
                                                                                                                                              (2, 'SUMMER20', NULL, 20.00, '2026-06-01 00:00:00', '2026-08-31 23:59:59', true, 50, 0, 5);

INSERT INTO statusi_porudzbine (status_istorija_id, porudzbina_id, status, vreme_promene, promenio_korisnik_id) VALUES
                                                                                                                    (1, 1, 'KREIRANA', '2026-05-25 09:08:06.068921', 5),
                                                                                                                    (2, 2, 'KREIRANA', '2026-05-25 09:08:18.871066', 5),
                                                                                                                    (3, 3, 'KREIRANA', '2026-05-25 09:09:25.310633', 5),
                                                                                                                    (4, 4, 'KREIRANA', '2026-05-25 09:09:32.691650', 5),
                                                                                                                    (5, 5, 'KREIRANA', '2026-05-25 09:10:02.493091', 5),
                                                                                                                    (6, 6, 'KREIRANA', '2026-05-25 09:10:14.984126', 5),
                                                                                                                    (7, 7, 'KREIRANA', '2026-05-25 09:10:29.991216', 5);

-- Pretrage
INSERT INTO pretrage (pretraga_id, tekst_upita, tip_pretrage, vreme_pretrage, korisnik_id) VALUES
                                                                                               (14, 'burger',      'OPSTA', '2026-05-26 09:15:00', 4),
                                                                                               (15, 'classic',     'OPSTA', '2026-05-26 09:16:10', 4),
                                                                                               (16, 'cola',        'OPSTA', '2026-05-26 11:30:00', 4),
                                                                                               (17, 'piće',        'OPSTA', '2026-05-26 11:31:05', 4),
                                                                                               (18, 'burger',      'OPSTA', '2026-05-27 12:05:00', 4),
                                                                                               (19, 'kafa',        'OPSTA', '2026-05-27 12:06:20', 4),
                                                                                               (20, 'wings',       'OPSTA', '2026-06-12 18:00:00', 4),
                                                                                               (21, 'buffalo',     'OPSTA', '2026-06-12 18:01:30', 4),
                                                                                               (22, 'monster',     'OPSTA', '2026-06-13 13:10:00', 4),
                                                                                               (23, 'cheesecake',  'OPSTA', '2026-06-13 20:00:00', 4),
                                                                                               (24, 'pizza',       'OPSTA', '2026-05-26 10:00:00', 5),
                                                                                               (25, 'capricciosa', 'OPSTA', '2026-05-26 10:01:15', 5),
                                                                                               (26, 'salata',      'OPSTA', '2026-05-26 12:45:00', 5),
                                                                                               (27, 'piletina',    'OPSTA', '2026-05-26 12:46:00', 5),
                                                                                               (28, 'cezar',       'OPSTA', '2026-05-27 11:00:00', 5),
                                                                                               (29, 'avokado',     'OPSTA', '2026-05-27 11:01:40', 5),
                                                                                               (30, 'pasta',       'OPSTA', '2026-05-27 19:30:00', 5),
                                                                                               (31, 'tiramisu',    'OPSTA', '2026-06-12 20:15:00', 5),
                                                                                               (32, 'desert',      'OPSTA', '2026-06-12 20:16:00', 5),
                                                                                               (33, 'marinara',    'OPSTA', '2026-06-13 13:00:00', 5),
                                                                                               (34, 'puttanesca',  'OPSTA', '2026-06-13 13:01:30', 5),
                                                                                               (35, 'hrskava',     'OPSTA', '2026-06-13 18:00:00', 5);

-- Klikovi
INSERT INTO klikovi (klik_id, tip_akcije, vreme_klika, korisnik_id, proizvod_id, kategorija_id) VALUES
                                                                                                    (49,  'REZULTAT_PRETRAGE', '2026-05-26 09:15:30', 4, 1,  NULL),
                                                                                                    (50,  'REZULTAT_PRETRAGE', '2026-05-26 09:15:31', 4, 2,  NULL),
                                                                                                    (51,  'PREGLED',           '2026-05-26 09:16:00', 4, 1,  NULL),
                                                                                                    (52,  'DETALJI',           '2026-05-26 09:16:20', 4, 1,  NULL),
                                                                                                    (53,  'PREGLED',           '2026-05-26 09:17:00', 4, 2,  NULL),
                                                                                                    (54,  'DETALJI',           '2026-05-26 09:17:30', 4, 2,  NULL),
                                                                                                    (55,  'KORPA',             '2026-05-26 09:18:00', 4, 1,  NULL),
                                                                                                    (56,  'PREGLED',           '2026-05-26 09:18:30', 4, NULL, 1),
                                                                                                    (57,  'REZULTAT_PRETRAGE', '2026-05-26 11:30:20', 4, 8,  NULL),
                                                                                                    (58,  'PREGLED',           '2026-05-26 11:30:45', 4, 8,  NULL),
                                                                                                    (59,  'KORPA',             '2026-05-26 11:31:00', 4, 8,  NULL),
                                                                                                    (60,  'PREGLED',           '2026-05-26 11:31:30', 4, NULL, 4),
                                                                                                    (61,  'PREGLED',           '2026-05-27 12:05:30', 4, 2,  NULL),
                                                                                                    (62,  'KORPA',             '2026-05-27 12:06:00', 4, 2,  NULL),
                                                                                                    (63,  'REZULTAT_PRETRAGE', '2026-05-27 12:06:45', 4, 9,  NULL),
                                                                                                    (64,  'PREGLED',           '2026-05-27 12:07:00', 4, 9,  NULL),
                                                                                                    (65,  'DETALJI',           '2026-05-27 12:07:20', 4, 9,  NULL),
                                                                                                    (66,  'KORPA',             '2026-05-27 12:07:40', 4, 9,  NULL),
                                                                                                    (67,  'DODAJ_OMILJENI',    '2026-05-27 12:08:00', 4, 2,  NULL),
                                                                                                    (68,  'REZULTAT_PRETRAGE', '2026-06-12 18:01:00', 4, 12, NULL),
                                                                                                    (69,  'PREGLED',           '2026-06-12 18:01:30', 4, 12, NULL),
                                                                                                    (70,  'DETALJI',           '2026-06-12 18:02:00', 4, 12, NULL),
                                                                                                    (71,  'KORPA',             '2026-06-12 18:02:30', 4, 12, NULL),
                                                                                                    (72,  'PREGLED',           '2026-06-12 18:03:00', 4, 13, NULL),
                                                                                                    (73,  'PREGLED',           '2026-06-12 18:03:30', 4, NULL, 6),
                                                                                                    (74,  'REZULTAT_PRETRAGE', '2026-06-12 20:00:10', 4, 11, NULL),
                                                                                                    (75,  'PREGLED',           '2026-06-12 20:00:30', 4, 11, NULL),
                                                                                                    (76,  'DETALJI',           '2026-06-12 20:01:00', 4, 11, NULL),
                                                                                                    (77,  'KORPA',             '2026-06-12 20:01:30', 4, 11, NULL),
                                                                                                    (78,  'DODAJ_OMILJENI',    '2026-06-12 20:02:00', 4, 11, NULL),
                                                                                                    (79,  'REZULTAT_PRETRAGE', '2026-06-13 13:10:30', 4, 2,  NULL),
                                                                                                    (80,  'PREGLED',           '2026-06-13 13:11:00', 4, 2,  NULL),
                                                                                                    (81,  'KORPA',             '2026-06-13 13:11:30', 4, 2,  NULL),
                                                                                                    (82,  'PREGLED',           '2026-06-13 13:12:00', 4, 8,  NULL),
                                                                                                    (83,  'KORPA',             '2026-06-13 13:12:30', 4, 8,  NULL),
                                                                                                    (84,  'PREGLED',           '2026-06-13 20:00:30', 4, 10, NULL),
                                                                                                    (85,  'PREGLED',           '2026-06-13 20:01:00', 4, 11, NULL),
                                                                                                    (86,  'KORPA',             '2026-06-13 20:01:30', 4, 11, NULL),
                                                                                                    (87,  'PREGLED',           '2026-06-13 20:02:00', 4, NULL, 5),
                                                                                                    (88,  'REZULTAT_PRETRAGE', '2026-05-26 10:01:30', 5, 3,  NULL),
                                                                                                    (89,  'REZULTAT_PRETRAGE', '2026-05-26 10:01:31', 5, 4,  NULL),
                                                                                                    (90,  'REZULTAT_PRETRAGE', '2026-05-26 10:01:32', 5, 5,  NULL),
                                                                                                    (91,  'PREGLED',           '2026-05-26 10:02:00', 5, 3,  NULL),
                                                                                                    (92,  'DETALJI',           '2026-05-26 10:02:30', 5, 3,  NULL),
                                                                                                    (93,  'KORPA',             '2026-05-26 10:03:00', 5, 3,  NULL),
                                                                                                    (94,  'DODAJ_OMILJENI',    '2026-05-26 10:03:30', 5, 3,  NULL),
                                                                                                    (95,  'PREGLED',           '2026-05-26 10:04:00', 5, NULL, 2),
                                                                                                    (96,  'REZULTAT_PRETRAGE', '2026-05-26 12:45:30', 5, 6,  NULL),
                                                                                                    (97,  'REZULTAT_PRETRAGE', '2026-05-26 12:45:31', 5, 7,  NULL),
                                                                                                    (98,  'PREGLED',           '2026-05-26 12:46:00', 5, 6,  NULL),
                                                                                                    (99,  'DETALJI',           '2026-05-26 12:46:30', 5, 6,  NULL),
                                                                                                    (100, 'KORPA',             '2026-05-26 12:47:00', 5, 6,  NULL),
                                                                                                    (101, 'PREGLED',           '2026-05-26 12:47:30', 5, NULL, 3),
                                                                                                    (102, 'REZULTAT_PRETRAGE', '2026-05-27 11:01:00', 5, 6,  NULL),
                                                                                                    (103, 'REZULTAT_PRETRAGE', '2026-05-27 11:01:01', 5, 7,  NULL),
                                                                                                    (104, 'PREGLED',           '2026-05-27 11:01:30', 5, 7,  NULL),
                                                                                                    (105, 'DETALJI',           '2026-05-27 11:02:00', 5, 7,  NULL),
                                                                                                    (106, 'KORPA',             '2026-05-27 11:02:30', 5, 7,  NULL),
                                                                                                    (107, 'DODAJ_OMILJENI',    '2026-05-27 11:03:00', 5, 7,  NULL),
                                                                                                    (108, 'REZULTAT_PRETRAGE', '2026-05-27 19:30:30', 5, 16, NULL),
                                                                                                    (109, 'PREGLED',           '2026-05-27 19:31:00', 5, 16, NULL),
                                                                                                    (110, 'DETALJI',           '2026-05-27 19:31:30', 5, 16, NULL),
                                                                                                    (111, 'KORPA',             '2026-05-27 19:32:00', 5, 16, NULL),
                                                                                                    (112, 'PREGLED',           '2026-05-27 19:32:30', 5, 13, NULL),
                                                                                                    (113, 'PREGLED',           '2026-05-27 19:33:00', 5, NULL, 6),
                                                                                                    (114, 'REZULTAT_PRETRAGE', '2026-06-12 20:15:30', 5, 10, NULL),
                                                                                                    (115, 'REZULTAT_PRETRAGE', '2026-06-12 20:15:31', 5, 11, NULL),
                                                                                                    (116, 'PREGLED',           '2026-06-12 20:16:00', 5, 10, NULL),
                                                                                                    (117, 'DETALJI',           '2026-06-12 20:16:30', 5, 10, NULL),
                                                                                                    (118, 'KORPA',             '2026-06-12 20:17:00', 5, 10, NULL),
                                                                                                    (119, 'DODAJ_OMILJENI',    '2026-06-12 20:17:30', 5, 10, NULL),
                                                                                                    (120, 'PREGLED',           '2026-06-12 20:18:00', 5, 11, NULL),
                                                                                                    (121, 'PREGLED',           '2026-06-12 20:18:30', 5, NULL, 5),
                                                                                                    (122, 'REZULTAT_PRETRAGE', '2026-06-13 13:01:00', 5, 4,  NULL),
                                                                                                    (123, 'REZULTAT_PRETRAGE', '2026-06-13 13:01:01', 5, 5,  NULL),
                                                                                                    (124, 'PREGLED',           '2026-06-13 13:02:00', 5, 5,  NULL),
                                                                                                    (125, 'DETALJI',           '2026-06-13 13:02:30', 5, 5,  NULL),
                                                                                                    (126, 'KORPA',             '2026-06-13 13:03:00', 5, 5,  NULL),
                                                                                                    (127, 'DODAJ_OMILJENI',    '2026-06-13 13:03:30', 5, 5,  NULL),
                                                                                                    (128, 'PREGLED',           '2026-06-13 13:04:00', 5, NULL, 2),
                                                                                                    (129, 'REZULTAT_PRETRAGE', '2026-06-13 18:00:30', 5, 13, NULL),
                                                                                                    (130, 'PREGLED',           '2026-06-13 18:01:00', 5, 13, NULL),
                                                                                                    (131, 'DETALJI',           '2026-06-13 18:01:30', 5, 13, NULL),
                                                                                                    (132, 'KORPA',             '2026-06-13 18:02:00', 5, 13, NULL),
                                                                                                    (133, 'PREGLED',           '2026-06-13 18:02:30', 5, 12, NULL),
                                                                                                    (134, 'PREGLED',           '2026-06-13 18:03:00', 5, 9,  NULL),
                                                                                                    (135, 'KORPA',             '2026-06-13 18:03:30', 5, 9,  NULL),
                                                                                                    (136, 'PREGLED',           '2026-06-13 18:04:00', 5, NULL, 6);

-- Omiljeni proizvodi
INSERT INTO omiljeni_proizvodi (korisnik_id, proizvod_id, datum_dodavanja) VALUES
                                                                               (4, 2,  '2026-05-27 12:08:00'),
                                                                               (4, 11, '2026-05-28 20:02:00'),
                                                                               (5, 3,  '2026-05-26 10:03:30'),
                                                                               (5, 7,  '2026-05-27 11:03:00'),
                                                                               (5, 5,  '2026-05-29 13:03:30'),
                                                                               (5, 10, '2026-05-28 20:17:30');

-- Omiljene kategorije
INSERT INTO omiljene_kategorije (korisnik_id, kategorija_id, datum_dodavanja) VALUES
                                                                                  (4, 1, '2026-05-26 09:18:30'),
                                                                                  (4, 4, '2026-05-26 11:31:30'),
                                                                                  (4, 6, '2026-05-28 18:03:30'),
                                                                                  (5, 2, '2026-05-26 10:04:00'),
                                                                                  (5, 3, '2026-05-26 12:47:30'),
                                                                                  (5, 6, '2026-05-30 18:04:00');

-- 1. Dodaj kolonu (Hibernate create-drop je pravi ionako, ali ovo
--    pokriva i slučaj ako se koristi validate/update mode)
ALTER TABLE kategorije
    ADD COLUMN IF NOT EXISTS tip_obroka VARCHAR(20);

-- 2. Mapiranje postojećih kategorija na tipove obroka
UPDATE kategorije SET tip_obroka = 'GLAVNO'   WHERE naziv ILIKE '%burger%';
UPDATE kategorije SET tip_obroka = 'GLAVNO'   WHERE naziv ILIKE '%pizza%';
UPDATE kategorije SET tip_obroka = 'PREDJELO' WHERE naziv ILIKE '%salad%';
UPDATE kategorije SET tip_obroka = 'PICE'     WHERE naziv ILIKE '%drink%';
UPDATE kategorije SET tip_obroka = 'DESERT'   WHERE naziv ILIKE '%desert%';
UPDATE kategorije SET tip_obroka = 'GLAVNO'   WHERE naziv ILIKE '%chicken%';
-- Sve kategorije bez eksplicitnog mapiranja dobijaju OSTALO
UPDATE kategorije SET tip_obroka = 'OSTALO'   WHERE tip_obroka IS NULL;

-- Sinhronizacija sekvenci za bazu
SELECT setval(pg_get_serial_sequence('korisnici', 'korisnik_id'), MAX(korisnik_id)) FROM korisnici;
SELECT setval(pg_get_serial_sequence('kategorije', 'kategorija_id'), MAX(kategorija_id)) FROM kategorije;
SELECT setval(pg_get_serial_sequence('alergeni', 'alergen_id'), MAX(alergen_id)) FROM alergeni;
SELECT setval(pg_get_serial_sequence('sastojci', 'sastojak_id'), MAX(sastojak_id)) FROM sastojci;
SELECT setval(pg_get_serial_sequence('proizvodi', 'proizvod_id'), MAX(proizvod_id)) FROM proizvodi;
SELECT setval(pg_get_serial_sequence('meniji', 'meni_id'), MAX(meni_id)) FROM meniji;
SELECT setval(pg_get_serial_sequence('stavke_menija', 'stavka_id'), MAX(stavka_id)) FROM stavke_menija;
SELECT setval(pg_get_serial_sequence('kuponi', 'kupon_id'), MAX(kupon_id)) FROM kuponi;
SELECT setval(pg_get_serial_sequence('porudzbine', 'porudzbina_id'), MAX(porudzbina_id)) FROM porudzbine;
SELECT setval(pg_get_serial_sequence('dostavljaci', 'korisnik_id'), MAX(korisnik_id)) FROM dostavljaci;
SELECT setval(pg_get_serial_sequence('statusi_porudzbine', 'status_istorija_id'), MAX(status_istorija_id)) FROM statusi_porudzbine;
SELECT setval(pg_get_serial_sequence('klikovi', 'klik_id'), MAX(klik_id)) FROM klikovi;
SELECT setval(pg_get_serial_sequence('pretrage', 'pretraga_id'), MAX(pretraga_id)) FROM pretrage;
SELECT setval(pg_get_serial_sequence('omiljeni_proizvodi', 'omiljeni_id'), MAX(omiljeni_id)) FROM omiljeni_proizvodi;
SELECT setval(pg_get_serial_sequence('omiljene_kategorije', 'omiljena_kategorija_id'), MAX(omiljena_kategorija_id)) FROM omiljene_kategorije;

-- ═══════════════════════════════════════════════════════════════════════════
-- Prikazane preporuke — istorijski seed podaci
--
-- Mapiranje stavke_menija -> proizvod_id:
--   stavka 1 -> proizvod 1  (Classic Burger)
--   stavka 2 -> proizvod 2  (Monster Burger)
--   stavka 5 -> proizvod 14 (Omelette Klasik)
--   stavka 6 -> proizvod 15 (Avocado Toast)
--   stavka 7 -> proizvod 3  (Pizza Capricciosa)
--   stavka 8 -> proizvod 4  (Pizza Marinara)
--   stavka 9 -> proizvod 9  (Domaca Kafa)
--   stavka 10-> proizvod 10 (Tiramisu)
--
-- Kupac 5 (Nenad) narucio u porudzbinama 1-7 na 2026-05-25:
--   porudzbina 1: proizvod 1, 2
--   porudzbina 2: proizvod 14, 15
--   porudzbina 3: proizvod 3, 4, 9, 10
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO prikazane_preporuke
(kupac_id, proizvod_id, tip_preporuke, prikazano_u, realizovano_u, uspesna)
VALUES
-- Kupac 5, sesija 1 (08:50) — personalizovane, pre porudzbina 1 i 2
(5,  1, 'PERSONALIZOVANA', '2026-05-25 08:50:00', '2026-05-25 09:08:06', true),
(5,  2, 'PERSONALIZOVANA', '2026-05-25 08:50:00', '2026-05-25 09:08:06', true),
(5, 14, 'PERSONALIZOVANA', '2026-05-25 08:50:00', '2026-05-25 09:08:18', true),
(5, 15, 'PERSONALIZOVANA', '2026-05-25 08:50:00', '2026-05-25 09:08:18', true),
(5,  6, 'PERSONALIZOVANA', '2026-05-25 08:50:00', NULL,                  false),
(5, 12, 'PERSONALIZOVANA', '2026-05-25 08:50:00', NULL,                  false),
-- Kupac 5, sesija 2 (09:05) — trend, pre porudzbine 3
(5,  3, 'TREND', '2026-05-25 09:05:00', '2026-05-25 09:09:25', true),
(5,  4, 'TREND', '2026-05-25 09:05:00', '2026-05-25 09:09:25', true),
(5,  9, 'TREND', '2026-05-25 09:05:00', '2026-05-25 09:09:25', true),
(5, 10, 'TREND', '2026-05-25 09:05:00', '2026-05-25 09:09:25', true),
(5, 11, 'TREND', '2026-05-25 09:05:00', NULL,                   false),
-- Kupac 5, sesija 3 (09:07) — sezonske
(5, 11, 'SEZONSKA', '2026-05-25 09:07:00', NULL, false),
(5,  7, 'SEZONSKA', '2026-05-25 09:07:00', NULL, false),
(5, 16, 'SEZONSKA', '2026-05-25 09:07:00', NULL, false),
-- Kupac 5, sesija 4 (09:08) — vremenske (samo proizvodi iz vremenskog menija: 14,15,3,4,9,10)
(5,  3, 'VREMENSKA', '2026-05-25 09:08:00', '2026-05-25 09:09:25', true),
(5,  9, 'VREMENSKA', '2026-05-25 09:08:00', '2026-05-25 09:09:25', true),
(5, 14, 'VREMENSKA', '2026-05-25 09:08:00', '2026-05-25 09:08:18', true),
(5, 15, 'VREMENSKA', '2026-05-25 09:08:00', '2026-05-25 09:08:18', true),
(5,  4, 'VREMENSKA', '2026-05-25 09:08:00', NULL,                  false),
(5, 10, 'VREMENSKA', '2026-05-25 09:08:00', NULL,                  false),
-- Kupac 4 (Marko) — pregledao, nije narucio
(4,  1, 'PERSONALIZOVANA', '2026-05-26 14:10:00', NULL, false),
(4,  3, 'PERSONALIZOVANA', '2026-05-26 14:10:00', NULL, false),
(4,  6, 'PERSONALIZOVANA', '2026-05-26 14:10:00', NULL, false),
(4, 12, 'PERSONALIZOVANA', '2026-05-26 14:10:00', NULL, false),
(4,  2, 'TREND',           '2026-05-26 14:11:00', NULL, false),
(4,  5, 'TREND',           '2026-05-26 14:11:00', NULL, false),
(4,  6, 'SEZONSKA',        '2026-05-26 14:12:00', NULL, false),
(4, 16, 'SEZONSKA',        '2026-05-26 14:12:00', NULL, false),
-- Kupac 5, korpa preporuke (prikazane dok je pregledao korpu pre porudzbine 3)
(5,  9, 'KORPA', '2026-05-25 09:06:00', '2026-05-25 09:09:25', true),
(5, 10, 'KORPA', '2026-05-25 09:06:00', '2026-05-25 09:09:25', true),
(5, 11, 'KORPA', '2026-05-25 09:06:00', NULL,                  false),
-- Kupac 4, korpa preporuke (nije narucio)
(4,  9, 'KORPA', '2026-05-26 14:13:00', NULL, false),
(4, 10, 'KORPA', '2026-05-26 14:13:00', NULL, false);

SELECT setval(pg_get_serial_sequence('prikazane_preporuke', 'id'), MAX(id)) FROM prikazane_preporuke;

-- ═══════════════════════════════════════════════════════════════════════════
-- Prikazani komboi — istorijski seed podaci
--
-- Stavke menija i njihovi proizvodi:
--   1->P1(ClassicBurger), 2->P2(MonsterBurger), 3->P12(BuffaloWings)
--   4->P8(CocaCola), 5->P14(Omelette), 6->P15(AvocadoToast)
--   7->P3(Capricciosa), 8->P4(Marinara), 9->P9(Kafa), 10->P10(Tiramisu)
--   11->P6(CezarSalata), 12->P7(AvocadoMediteran), 14->P11(Cheesecake)
--
-- Kupac 5 je narucio stavke: 1,2,5,6,7,8,9,10
-- ═══════════════════════════════════════════════════════════════════════════

-- Kombo 1: Burger+Wings+Kola (stavke 1,3,4) — kupac 5 narucio 1/3 (samo burger)
INSERT INTO prikazani_komboi (kupac_id, prikazano_u, realizovano_u, uspesna, broj_narucenih_stavki)
VALUES (5, '2026-05-25 08:49:00', '2026-05-25 09:08:06', true, 1);
INSERT INTO prikazani_kombo_stavke (kombo_id, stavka_menija_id)
VALUES (1, 1), (1, 3), (1, 4);

-- Kombo 2: Pizza+Kafa+Tiramisu (stavke 7,9,10) — kupac 5 narucio 3/3 (sve)
INSERT INTO prikazani_komboi (kupac_id, prikazano_u, realizovano_u, uspesna, broj_narucenih_stavki)
VALUES (5, '2026-05-25 09:04:00', '2026-05-25 09:09:25', true, 3);
INSERT INTO prikazani_kombo_stavke (kombo_id, stavka_menija_id)
VALUES (2, 7), (2, 9), (2, 10);

-- Kombo 3: Omelette+AvocadoToast+Kafa (stavke 5,6,9) — kupac 5 narucio 3/3
INSERT INTO prikazani_komboi (kupac_id, prikazano_u, realizovano_u, uspesna, broj_narucenih_stavki)
VALUES (5, '2026-05-25 08:49:00', '2026-05-25 09:08:18', true, 3);
INSERT INTO prikazani_kombo_stavke (kombo_id, stavka_menija_id)
VALUES (3, 5), (3, 6), (3, 9);

-- Kombo 4: CezarSalata+Wings+Cheesecake (stavke 11,3,14) — kupac 5 narucio 0/3 (nista)
INSERT INTO prikazani_komboi (kupac_id, prikazano_u, realizovano_u, uspesna, broj_narucenih_stavki)
VALUES (5, '2026-05-25 09:04:00', NULL, false, 0);
INSERT INTO prikazani_kombo_stavke (kombo_id, stavka_menija_id)
VALUES (4, 11), (4, 3), (4, 14);

-- Kombo 5: MonsterBurger+Pizza+Tiramisu (stavke 2,8,10) — kupac 5 narucio 3/3
INSERT INTO prikazani_komboi (kupac_id, prikazano_u, realizovano_u, uspesna, broj_narucenih_stavki)
VALUES (5, '2026-05-25 09:05:00', '2026-05-25 09:09:25', true, 3);
INSERT INTO prikazani_kombo_stavke (kombo_id, stavka_menija_id)
VALUES (5, 2), (5, 8), (5, 10);

-- Kombo 6: kupac 4 — nije narucio nista
INSERT INTO prikazani_komboi (kupac_id, prikazano_u, realizovano_u, uspesna, broj_narucenih_stavki)
VALUES (4, '2026-05-26 14:10:00', NULL, false, 0);
INSERT INTO prikazani_kombo_stavke (kombo_id, stavka_menija_id)
VALUES (6, 7), (6, 9), (6, 14);

SELECT setval(pg_get_serial_sequence('prikazani_komboi', 'id'), MAX(id)) FROM prikazani_komboi;