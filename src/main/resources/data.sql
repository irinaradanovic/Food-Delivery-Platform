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




