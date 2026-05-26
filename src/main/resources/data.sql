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
                                                                             (3, 'Green Garden Healthy', 'Fruškogorska 8, Novi Sad', '021-666-777', 3);

-- Kategorije
INSERT INTO kategorije (kategorija_id, naziv) VALUES
                                                  (1, 'Burgers'),
                                                  (2, 'Pizzas'),
                                                  (3, 'Salads'),
                                                  (4, 'Drinks');

-- Alergeni
INSERT INTO alergeni (alergen_id, naziv) VALUES
                                             (1, 'Gluten'),
                                             (2, 'Kikiriki'),
                                             (3, 'Laktoza'),
                                             (4, 'Jaja');

-- Sastojci
INSERT INTO sastojci (sastojak_id, naziv) VALUES
                                              (1, 'Govedina'),
                                              (2, 'Mocarela'),
                                              (3, 'Pelat'),
                                              (4, 'Cezar sos'),
                                              (5, 'Zelena salata'),
                                              (6, 'Čedar sir');

-- Proizvodi
INSERT INTO proizvodi (proizvod_id, naziv, opis, kalorije, cena, fotografija, kolicina, merna_jedinica, kategorija_id) VALUES
                                                                                                                           (1, 'Classic Burger', 'Sočna govedina sa čedar sirom', 650, 550.00, 'burger.jpg', 350, 'g', 1),
                                                                                                                           (2, 'Pizza Capricciosa', 'Klasična pica sa šunkom i sirom', 800, 780.00, 'capricciosa.jpg', 500, 'g', 2),
                                                                                                                           (3, 'Cezar Salata', 'Sveža salata sa piletinom i krutonima', 450, 490.00, 'cezar.jpg', 400, 'g', 3);

-- Povezivanje Proizvoda sa Sastojcima
INSERT INTO proizvod_sastojci (proizvod_id, sastojak_id) VALUES
                                                             (1, 1), (1, 6),
                                                             (2, 2), (2, 3),
                                                             (3, 4), (3, 5);

-- Povezivanje Proizvoda sa Alergenima
INSERT INTO proizvod_alergeni (proizvod_id, allergen_id) VALUES
                                                             (1, 1), (1, 3),
                                                             (2, 1), (2, 3),
                                                             (3, 4);

-- Meniji
-- Restoran 1 ima STANDARDNI meni
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, restoran_id) VALUES
    (1, 'Glavni Meni Big Bite', 'Standardna ponuda hrane i pića', 'STANDARDNI', 'v1', true, '2026-05-26', 1);

-- Restoran 2 ima VREMENSKI meni (Doručak od 08:00 do 11:00)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, vreme_od, vreme_do, restoran_id) VALUES
    (2, 'Jutarnji Meni', 'Ponuda za doručak', 'VREMENSKI', 'v1', true, '2026-05-26', '08:00:00', '11:00:00', 2);

-- Restoran 3 ima SEZONSKI meni (Letnji meni sa datumima)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, pocetak_sezone, kraj_sezone, restoran_id) VALUES
    (3, 'Letnji Meni 2026', 'Laka letnja osveženja', 'SEZONSKI', 'v1', true, '2026-05-26', '2026-06-01', '2026-08-31', 3);


-- Povezivanje Menija i Proizvoda sa cenama na meniju
INSERT INTO stavke_menija (stavka_id, meni_id, proizvod_id, cena, dostupno, vreme_pripreme_min, vreme_pripreme_max) VALUES
                                                                                                                        (1, 1, 1, 550.00, true, 10, 15),
                                                                                                                        (2, 2, 2, 750.00, true, 12, 20),
                                                                                                                        (3, 3, 3, 490.00, true, 5, 10);

SELECT setval(pg_get_serial_sequence('korisnici', 'korisnik_id'), MAX(korisnik_id)) FROM korisnici;

--Kupac
-- =========================================================================
-- 1. KREIRANJE TABELA (STRUKTURA)
-- =========================================================================

CREATE TABLE kategorije (
                            kategorija_id bigint NOT NULL PRIMARY KEY,
                            naziv character varying(255)
);

CREATE TABLE korisnici (
                           korisnik_id bigint NOT NULL PRIMARY KEY,
                           datum_reg character varying(255),
                           email character varying(255),
                           ime character varying(255),
                           lozinka character varying(255),
                           prezime character varying(255),
                           telefon character varying(255),
                           uloga character varying(255)
);

CREATE TABLE kupci (
                       korisnik_id bigint NOT NULL PRIMARY KEY REFERENCES korisnici(korisnik_id),
                       adresa character varying(255),
                       broj_kartice character varying(255)
);

CREATE TABLE proizvodi (
                           proizvod_id bigint NOT NULL PRIMARY KEY,
                           naziv character varying(255),
                           opis character varying(255),
                           cena numeric(38,2) DEFAULT 0.00,
                           kolicina numeric(38,2),
                           merna_jedinica character varying(255),
                           kalorije numeric(38,2),
                           fotografija character varying(255),
                           kategorija_id bigint REFERENCES kategorije(kategorija_id)
);

CREATE TABLE porudzbine (
                            porudzbinа_id bigint NOT NULL PRIMARY KEY,
                            korisnik_id bigint NOT NULL REFERENCES kupci(korisnik_id),
                            ukupna_cena numeric(38,2),
                            status character varying(255),
                            tip_dostave character varying(255),
                            adresa_dostave character varying(255),
                            datum_kreiranja timestamp without time zone,
                            kupon_kod character varying(255),
                            napomena character varying(255)
);

CREATE TABLE stavke_porudzbine (
                                   stavka_id bigint NOT NULL PRIMARY KEY,
                                   porudzbina_id bigint NOT NULL REFERENCES porudzbine(porudzbinа_id),
                                   proizvod_id bigint NOT NULL REFERENCES proizvodi(proizvod_id),
                                   kolicina integer,
                                   cena numeric(38,2)
);

CREATE TABLE klikovi (
                         klik_id bigint NOT NULL PRIMARY KEY,
                         korisnik_id bigint NOT NULL REFERENCES kupci(korisnik_id),
                         proizvod_id bigint NOT NULL REFERENCES proizvodi(proizvod_id),
                         tip_akcije character varying(255),
                         vreme_klika timestamp without time zone
);

CREATE TABLE pretrage (
                          pretraga_id bigint NOT NULL PRIMARY KEY,
                          korisnik_id bigint NOT NULL REFERENCES kupci(korisnik_id),
                          tekst_upita character varying(255),
                          tip_pretrage character varying(255),
                          vreme_pretrage timestamp without time zone
);

CREATE TABLE omiljene_kategorije (
                                     omiljena_kategorija_id bigint NOT NULL PRIMARY KEY,
                                     korisnik_id bigint NOT NULL REFERENCES kupci(korisnik_id),
                                     kategorija_id bigint NOT NULL REFERENCES kategorije(kategorija_id),
                                     datum_dodavanja timestamp without time zone,
                                     CONSTRAINT uk_korisnik_kategorija UNIQUE (korisnik_id, kategorija_id)
);

CREATE TABLE omiljeni_proizvodi (
                                    omiljeni_id bigint NOT NULL PRIMARY KEY,
                                    korisnik_id bigint NOT NULL REFERENCES kupci(korisnik_id),
                                    proizvod_id bigint NOT NULL REFERENCES proizvodi(proizvod_id),
                                    datum_dodavanja timestamp without time zone,
                                    CONSTRAINT uk_korisnik_proizvod UNIQUE (korisnik_id, proizvod_id)
);

-- =========================================================================
-- 2. UBACIVANJE PODATAKA (INSERT NAREDBE)
-- =========================================================================

-- Podaci: kategorije
INSERT INTO kategorije VALUES (1, 'Pizza');
INSERT INTO kategorije VALUES (2, 'Burgeri');
INSERT INTO kategorije VALUES (3, 'Pasta');
INSERT INTO kategorije VALUES (4, 'Piletina');
INSERT INTO kategorije VALUES (5, 'Salate');
INSERT INTO kategorije VALUES (6, 'Deserti');
INSERT INTO kategorije VALUES (7, 'Pića');
INSERT INTO kategorije VALUES (8, 'Ostalo');

-- Podaci: korisnici
INSERT INTO korisnici VALUES (1, NULL, 'markovic@gmail.com', 'Marko', '123456', 'Markovic', '064235768', 'KUPAC');
INSERT INTO korisnici VALUES (2, NULL, 'lalic@gmail.com', 'Nenad', '123456', 'Lalic', '06789256812', 'KUPAC');

-- Podaci: kupci
INSERT INTO kupci VALUES ('Narodnog fronta 16, Novi Sad', NULL, 1);
INSERT INTO kupci VALUES ('Narodnog fronta 19, Novi Sad', NULL, 2);

-- Podaci: proizvodi
INSERT INTO proizvodi VALUES (1, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=600&h=400&fit=crop&auto=format', 850.00, 450.00, 'g', 'Margherita', 'Klasična pizza sa paradajz sosom, mocarelom i bosiljkom', 1, 850.00);
INSERT INTO proizvodi VALUES (2, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&h=400&fit=crop&auto=format', 980.00, 500.00, 'g', 'Quattro Formaggi', 'Pizza sa četiri vrste sira — mocarela, gorgonzola, parmezan, čeder', 1, 980.00);
INSERT INTO proizvodi VALUES (3, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=600&h=400&fit=crop&auto=format', 1050.00, 480.00, 'g', 'Pepperoni', 'Pizza sa pikantnom pepperoni kobasicom i mocarelom', 1, 1050.00);
INSERT INTO proizvodi VALUES (4, 'https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?w=600&h=400&fit=crop&auto=format', 780.00, 520.00, 'g', 'Vegetariana', 'Pizza sa povrćem — paprika, tikvice, pečurke, masline', 1, 780.00);
INSERT INTO proizvodi VALUES (5, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&h=400&fit=crop&auto=format', 750.00, 300.00, 'g', 'Classic Burger', 'Goveđi burger sa salatom, paradajzom, krastavcem i kečapom', 2, 750.00);
INSERT INTO proizvodi VALUES (6, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=600&h=400&fit=crop&auto=format', 950.00, 400.00, 'g', 'BBQ Bacon Burger', 'Dupli burger sa slaninom, čedar sirom i BBQ sosom', 2, 950.00);
INSERT INTO proizvodi VALUES (7, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=600&h=400&fit=crop&auto=format', 820.00, 350.00, 'g', 'Crispy Chicken Burger', 'Pohana piletina, coleslaw salata i majonez', 2, 820.00);
INSERT INTO proizvodi VALUES (8, 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=600&h=400&fit=crop&auto=format', 680.00, 280.00, 'g', 'Veggie Burger', 'Burger od sočiva sa avokadom i rukola salatom', 2, 680.00);
INSERT INTO proizvodi VALUES (9, 'https://images.unsplash.com/photo-1551892374-ecf8754cf8b0?w=600&h=400&fit=crop&auto=format', 720.00, 400.00, 'g', 'Spaghetti Bolognese', 'Špagete sa klasičnim bolonjez sosom od mlevenog mesa', 3, 720.00);
INSERT INTO proizvodi VALUES (10, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=600&h=400&fit=crop&auto=format', 650.00, 350.00, 'g', 'Penne Arrabiata', 'Pene sa pikantnim paradajz sosom i belim lukom', 3, 650.00);
INSERT INTO proizvodi VALUES (11, 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600&h=400&fit=crop&auto=format', 880.00, 420.00, 'g', 'Carbonara', 'Špagete sa kremastim sosom od jaja, pančete i parmezana', 3, 880.00);
INSERT INTO proizvodi VALUES (12, 'https://images.unsplash.com/photo-1473093226795-af9932fe5856?w=600&h=400&fit=crop&auto=format', 710.00, 380.00, 'g', 'Pasta Pesto', 'Pasta sa domaćim pesto sosom od bosiljka i pinjola', 3, 710.00);
INSERT INTO proizvodi VALUES (13, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=600&h=400&fit=crop&auto=format', 620.00, 280.00, 'g', 'Chicken Wings', 'Hrskava piletina u pikantnom sosu, 8 komada', 4, 620.00);
INSERT INTO proizvodi VALUES (14, 'https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=600&h=400&fit=crop&auto=format', 540.00, 450.00, 'g', 'Grilled Chicken', 'Grilovana piletina sa povrćem i tzatziki sosom', 4, 540.00);
INSERT INTO proizvodi VALUES (15, 'https://images.unsplash.com/photo-1562967914-608f82629710?w=600&h=400&fit=crop&auto=format', 580.00, 200.00, 'g', 'Chicken Nuggets', 'Hrskavi nuggets od pilećeg filea, 10 komada', 4, 580.00);
INSERT INTO proizvodi VALUES (16, 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=600&h=400&fit=crop&auto=format', 760.00, 450.00, 'g', 'Butter Chicken', 'Kremasti indijski kari sa piletinom i basmati pirinčem', 4, 760.00);
INSERT INTO proizvodi VALUES (17, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=600&h=400&fit=crop&auto=format', 480.00, 350.00, 'g', 'Caesar Salata', 'Rimska salata, pileće meso, krutoni, parmezan i Caesar dresing', 5, 480.00);
INSERT INTO proizvodi VALUES (18, 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=600&h=400&fit=crop&auto=format', 380.00, 300.00, 'g', 'Grčka Salata', 'Paradajz, krastavac, feta sir, masline i origano', 5, 380.00);
INSERT INTO proizvodi VALUES (19, 'https://images.unsplash.com/photo-1592417817098-8fd3d9eb14a5?w=600&h=400&fit=crop&auto=format', 420.00, 280.00, 'g', 'Caprese', 'Mocarela, paradajz, bosiljak i maslinovo ulje', 5, 420.00);
INSERT INTO proizvodi VALUES (20, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600&h=400&fit=crop&auto=format', 450.00, 150.00, 'g', 'Tiramisu', 'Klasični italijanski desert sa mascarpone kremom i kafom', 6, 450.00);
INSERT INTO proizvodi VALUES (21, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=600&h=400&fit=crop&auto=format', 520.00, 180.00, 'g', 'Cheesecake', 'Njujorški cheesecake sa šumskim voćem', 6, 520.00);
INSERT INTO proizvodi VALUES (22, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&h=400&fit=crop&auto=format', 580.00, 150.00, 'g', 'Čokoladna torta', 'Bogata čokoladna torta sa ganache prelivom', 6, 580.00);
INSERT INTO proizvodi VALUES (23, 'https://images.unsplash.com/photo-1519676867240-f03562e64548?w=600&h=400&fit=crop&auto=format', 490.00, 160.00, 'g', 'Palačinke', 'Domaće palačinke sa Nutellom i bananama', 6, 490.00);
INSERT INTO proizvodi VALUES (24, 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=600&h=400&fit=crop&auto=format', 139.00, 330.00, 'ml', 'Coca-Cola', 'Osvežavajuća Coca-Cola, 330ml', 7, 139.00);
INSERT INTO proizvodi VALUES (25, 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e?w=600&h=400&fit=crop&auto=format', 135.00, 330.00, 'ml', 'Pepsi', 'Ledena Pepsi, 330ml', 7, 135.00);
INSERT INTO proizvodi VALUES (26, 'https://bellyfull.net/wp-content/uploads/2022/05/Lemonade-blog-2.jpg', 120.00, 500.00, 'ml', 'Limunada', 'Domaća limunada sa mentom i ledom, 500ml', 7, 120.00);
INSERT INTO proizvodi VALUES (27, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=600&h=400&fit=crop&auto=format', 180.00, 400.00, 'ml', 'Sok od narandže', 'Sveže ceđeni sok od narandže, 400ml', 7, 180.00);
INSERT INTO proizvodi VALUES (28, 'https://i.pinimg.com/originals/1f/43/e2/1f43e25c47358dfcd505bc39913c60fe.jpg', 0.00, 500.00, 'ml', 'Voda', 'Negazirana mineralna voda, 500ml', 7, 0.00);
INSERT INTO proizvodi VALUES (29, 'https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=600&h=400&fit=crop&auto=format', 310.00, 330.00, 'g', 'Riblja čorba', 'Tradicionalna riblja čorba', 8, 310.00);
INSERT INTO proizvodi VALUES (30, 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=600&h=400&fit=crop&auto=format', 620.00, 300.00, 'g', 'Takosi', 'Meksički takosi sa mesom i povrćem, 3 komada', 8, 620.00);
INSERT INTO proizvodi VALUES (31, 'https://png.pngtree.com/png-vector/20241102/ourmid/pngtree-classic-homemade-cornbread-slice-with-a-golden-crust-on-white-background-png-image_14232559.png', 260.00, 120.00, 'g', 'Proja sa sirom', 'Domaća proja sa sitnim sirom', 8, 260.00);
INSERT INTO proizvodi VALUES (32, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600&h=400&fit=crop&auto=format', 890.00, 600.00, 'g', 'Mešano meso sa roštilja', 'Bogata porcija mešanog mesa sa roštilja', 8, 890.00);

-- Podaci: porudzbine
INSERT INTO porudzbine VALUES (1, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:08:06.068921', NULL, '', 'KREIRANA', 'STANDARDNA', 9.80, 2);
INSERT INTO porudzbine VALUES (2, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:08:18.871066', NULL, '2. sprat', 'KREIRANA', 'STANDARDNA', 9.80, 2);
INSERT INTO porudzbine VALUES (3, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:09:25.310633', NULL, '2. sprat', 'KREIRANA', 'STANDARDNA', 9.80, 2);
INSERT INTO porudzbine VALUES (4, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:09:32.69165', NULL, '2. sprat', 'KREIRANA', 'STANDARDNA', 9.80, 2);
INSERT INTO porudzbine VALUES (5, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:10:02.493091', NULL, '2. sprat', 'KREIRANA', 'STANDARDNA', 9.80, 2);
INSERT INTO porudzbine VALUES (6, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:10:14.984126', NULL, '2. sprat', 'KREIRANA', 'STANDARDNA', 9.80, 2);
INSERT INTO porudzbine VALUES (7, 'Narodnog Fronta 7, Novi Sad', '2026-05-25 09:10:29.991216', NULL, '2. sprat', 'KREIRANA', 'STANDARDNA', 9.80, 2);

-- Podaci: stavke_porudzbine
INSERT INTO stavke_porudzbine VALUES (1, 9.80, 1, 1, 2);
INSERT INTO stavke_porudzbine VALUES (2, 9.80, 1, 2, 2);
INSERT INTO stavke_porudzbine VALUES (3, 9.80, 1, 3, 2);
INSERT INTO stavke_porudzbine VALUES (4, 9.80, 1, 4, 2);
INSERT INTO stavke_porudzbine VALUES (5, 9.80, 1, 5, 2);
INSERT INTO stavke_porudzbine VALUES (6, 9.80, 1, 6, 2);
INSERT INTO stavke_porudzbine VALUES (7, 9.80, 1, 7, 2);

-- Podaci: klikovi
INSERT INTO klikovi VALUES (1, 'PREGLED', '2026-05-24 11:18:19.77089', 2, 5);
INSERT INTO klikovi VALUES (2, 'PREGLED', '2026-05-24 11:18:20.970097', 2, 5);
INSERT INTO klikovi VALUES (3, 'PREGLED', '2026-05-24 11:18:21.96425', 2, 5);
INSERT INTO klikovi VALUES (4, 'PREGLED', '2026-05-24 11:18:23.339181', 2, 9);
INSERT INTO klikovi VALUES (5, 'PREGLED', '2026-05-24 11:18:23.587001', 2, 9);
INSERT INTO klikovi VALUES (6, 'PREGLED', '2026-05-24 11:20:37.208674', 2, 11);
INSERT INTO klikovi VALUES (7, 'PREGLED', '2026-05-24 12:21:14.605071', 2, 14);
INSERT INTO klikovi VALUES (8, 'PREGLED', '2026-05-24 12:21:15.745218', 2, 14);
INSERT INTO klikovi VALUES (9, 'PREGLED', '2026-05-24 13:09:53.582406', 2, 23);
INSERT INTO klikovi VALUES (10, 'PREGLED', '2026-05-24 13:13:51.795557', 2, 32);
INSERT INTO klikovi VALUES (11, 'PREGLED', '2026-05-24 13:13:52.990959', 2, 32);
INSERT INTO klikovi VALUES (12, 'PREGLED', '2026-05-24 13:15:06.971218', 2, 32);
INSERT INTO klikovi VALUES (13, 'PREGLED', '2026-05-24 13:19:02.19047', 2, 31);
INSERT INTO klikovi VALUES (14, 'PREGLED', '2026-05-24 13:19:04.136781', 2, 31);
INSERT INTO klikovi VALUES (15, 'PREGLED', '2026-05-24 13:20:56.377343', 2, 28);
INSERT INTO klikovi VALUES (16, 'PREGLED', '2026-05-24 13:21:36.645282', 2, 26);
INSERT INTO klikovi VALUES (17, 'PREGLED', '2026-05-24 13:22:46.494394', 2, 28);
INSERT INTO klikovi VALUES (18, 'PREGLED', '2026-05-24 13:46:10.569618', 2, 2);
INSERT INTO klikovi VALUES (19, 'PREGLED', '2026-05-24 18:22:09.433661', 2, 26);
INSERT INTO klikovi VALUES (20, 'KORPA', '2026-05-24 18:22:20.19602', 2, 26);
INSERT INTO klikovi VALUES (21, 'KORPA', '2026-05-24 18:22:31.277277', 2, 26);
INSERT INTO klikovi VALUES (22, 'PREGLED', '2026-05-24 18:22:49.912077', 2, 1);
INSERT INTO klikovi VALUES (23, 'KORPA', '2026-05-25 09:07:22.400348', 2, 2);
INSERT INTO klikovi VALUES (24, 'PREGLED', '2026-05-25 09:12:20.191611', 2, 11);
INSERT INTO klikovi VALUES (25, 'KORPA', '2026-05-25 09:12:49.583981', 2, 13);
INSERT INTO klikovi VALUES (26, 'KORPA', '2026-05-25 09:12:53.837157', 2, 13);
INSERT INTO klikovi VALUES (27, 'PREGLED', '2026-05-25 09:18:08.261056', 2, 1);
INSERT INTO klikovi VALUES (28, 'KORPA', '2026-05-25 09:45:03.390327', 2, 2);
INSERT INTO resignation INSERT INTO klikovi VALUES (29, 'KORPA', '2026-05-25 09:59:46.08506', 2, 26);
INSERT INTO klikovi VALUES (30, 'KORPA', '2026-05-25 10:00:06.103191', 2, 2);
INSERT INTO klikovi VALUES (31, 'KORPA', '2026-05-25 10:17:53.926342', 2, 26);
INSERT INTO klikovi VALUES (32, 'KORPA', '2026-05-25 10:23:38.663827', 2, 2);
INSERT INTO klikovi VALUES (33, 'PREGLED', '2026-05-25 10:24:28.563324', 2, 26);
INSERT INTO klikovi VALUES (34, 'KORPA', '2026-05-25 18:27:16.045984', 2, 2);
INSERT INTO klikovi VALUES (35, 'PLACANJE', '2026-05-25 18:27:18.820107', 2, 2);
INSERT INTO klikovi VALUES (36, 'KUPOVINA', '2026-05-25 18:27:18.830747', 2, 2);
INSERT INTO klikovi VALUES (37, 'PREGLED', '2026-05-25 18:35:28.70401', 2, 14);
INSERT INTO klikovi VALUES (38, 'PREGLED', '2026-05-25 18:45:38.15342', 2, 2);
INSERT INTO klikovi VALUES (39, 'DODAJ_OMILJENI', '2026-05-25 23:36:50.74754', 2, 2);
INSERT INTO klikovi VALUES (40, 'UKLONI_OMILJENI', '2026-05-25 23:36:51.352762', 2, 2);
INSERT INTO klikovi VALUES (41, 'KORPA', '2026-05-25 23:36:55.221134', 2, 2);
INSERT INTO klikovi VALUES (42, 'UKLONI_IZ_KORPE', '2026-05-25 23:37:01.971029', 2, 2);
INSERT INTO klikovi VALUES (43, 'REZULTAT_PRETRAGE', '2026-05-25 23:38:32.146358', 2, 26);
INSERT INTO klikovi VALUES (44, 'DODAJ_OMILJENI', '2026-05-25 23:53:18.022442', 2, 4);
INSERT INTO klikovi VALUES (45, 'UKLONI_OMILJENI', '2026-05-25 23:53:18.753286', 2, 4);
INSERT INTO klikovi VALUES (46, 'DETALJI', '2026-05-25 23:53:19.440917', 2, 4);
INSERT INTO klikovi VALUES (47, 'KORPA', '2026-05-25 23:53:24.247347', 2, 4);
INSERT INTO klikovi VALUES (48, 'UKLONI_IZ_KORPE', '2026-05-25 23:53:27.653239', 2, 4);

-- Podaci: pretrage
INSERT INTO pretrage VALUES (1, 'pica', 'OPSTA', '2026-05-24 10:43:09.564465', 1);
INSERT INTO pretrage VALUES (2, 'marg', 'OPSTA', '2026-05-24 11:19:54.241524', 2);
INSERT INTO pretrage VALUES (3, 'margherita', 'OPSTA', '2026-05-24 11:20:06.445416', 2);
INSERT INTO pretrage VALUES (4, 'carbonara', 'OPSTA', '2026-05-24 11:20:23.631874', 2);
INSERT INTO pretrage VALUES (5, 'Carbonara', 'OPSTA', '2026-05-24 11:20:28.330843', 2);
INSERT INTO pretrage VALUES (6, 'Carbonara', 'OPSTA', '2026-05-24 11:20:42.420398', 2);
INSERT INTO pretrage VALUES (7, 'carbonara', 'OPSTA', '2026-05-24 11:31:22.584595', 2);
INSERT INTO pretrage VALUES (8, 'carbonara', 'OPSTA', '2026-05-24 11:31:22.584595', 2);
INSERT INTO pretrage VALUES (9, 'margherita', 'OPSTA', '2026-05-24 13:34:29.974048', 2);
INSERT INTO pretrage VALUES (10, 'marg', 'OPSTA', '2026-05-24 13:34:34.080257', 2);
INSERT INTO pretrage VALUES (11, 'car', 'OPSTA', '2026-05-24 13:34:36.56451', 2);
INSERT INTO pretrage VALUES (12, 'limunada', 'OPSTA', '2026-05-25 23:38:32.113215', 2);
INSERT INTO pretrage VALUES (13, 'hygui', 'OPSTA', '2026-05-25 23:39:20.161768', 2);

-- Podaci: omiljene_kategorije
INSERT INTO omiljene_kategorije VALUES (1, '2026-05-24 12:19:57.447502', 3, 2);
INSERT INTO omiljene_kategorije VALUES (2, '2026-05-24 12:32:55.337159', 5, 2);

-- Podaci: omiljeni_proizvodi
INSERT INTO omiljeni_proizvodi VALUES (9, '2026-05-24 11:27:48.164161', 2, 1);
INSERT INTO omiljeni_proizvodi VALUES (12, '2026-05-25 10:24:59.061989', 2, 25);