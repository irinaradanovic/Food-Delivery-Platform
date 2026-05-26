-- Menadzeri
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES
                                                                                                 (1, 'Marko', 'Marković', '064123456', 'password123', 'menadzer1@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (2, 'Nikola', 'Nikolić', '065987654', 'password123', 'menadzer2@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (3, 'Jovana', 'Jovanić', '063111222', 'password123', 'menadzer3@test.com', '2026-05-25', 'MENADZER');

INSERT INTO menadzeri (korisnik_id) VALUES (1), (2), (3);

-- Kupci
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES (4, NULL, 'markovic@gmail.com', 'Marko', '123456', 'Markovic', '064235768', 'KUPAC');
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES (5, NULL, 'lalic@gmail.com', 'Nenad', '123456', 'Lalic', '06789256812', 'KUPAC');

/*INSERT INTO kupci VALUES ('Narodnog fronta 16, Novi Sad', NULL, 4);
INSERT INTO kupci VALUES ('Narodnog fronta 19, Novi Sad', NULL, 5); */
-- Kupci su integrisani na kraju fajla (korisnik_id 4 i 5, zbog konflikta sa menadzerima 1-3)




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


-- omiljene_kategorije
/*INSERT INTO omiljene_kategorije VALUES (1, '2026-05-24 12:19:57.447502', 3, 2);
INSERT INTO omiljene_kategorije VALUES (2, '2026-05-24 12:32:55.337159', 5, 2);

-- omiljeni_proizvodi
INSERT INTO omiljeni_proizvodi VALUES (9, '2026-05-24 11:27:48.164161', 2, 1);
INSERT INTO omiljeni_proizvodi VALUES (12, '2026-05-25 10:24:59.061989', 2, 25); */
-- Napomena: korisnik 2 (iz kupac bloka) = korisnik_id 5 u integraciji
-- kategorija 3 (Pasta) -> 9, kategorija 5 (Salate) -> 11
INSERT INTO omiljene_kategorije VALUES (1, '2026-05-24 12:19:57.447502', 9, 5);
INSERT INTO omiljene_kategorije VALUES (2, '2026-05-24 12:32:55.337159', 11, 5);

-- omiljeni_proizvodi
-- korisnik 2 -> 5, proizvod 1 -> 17 (Margherita), proizvod 25 -> 41 (Pepsi)
INSERT INTO omiljeni_proizvodi VALUES (9, '2026-05-24 11:27:48.164161', 5, 17);
INSERT INTO omiljeni_proizvodi VALUES (12, '2026-05-25 10:24:59.061989', 5, 41);

-- =========================================================================
-- INTEGRISANI PODACI IZ KUPAC MODULA
-- Mapiranja:
--   korisnici: stari 1->4, stari 2->5
--   kategorije: stari 1-8 -> novi 7-14
--   proizvodi: stari 1-32 -> novi 17-48
-- =========================================================================

-- Kupci (korisnici)
INSERT INTO korisnici (korisnik_id, datum_reg, email, ime, lozinka, prezime, telefon, uloga)
VALUES (4, NULL, 'markovic@gmail.com', 'Marko', '123456', 'Markovic', '064235768', 'KUPAC')
ON CONFLICT (korisnik_id) DO NOTHING;
INSERT INTO korisnici (korisnik_id, datum_reg, email, ime, lozinka, prezime, telefon, uloga)
VALUES (5, NULL, 'lalic@gmail.com', 'Nenad', '123456', 'Lalic', '06789256812', 'KUPAC')
ON CONFLICT (korisnik_id) DO NOTHING;

INSERT INTO kupci (korisnik_id, adresa, broj_kartice) VALUES (4, 'Narodnog fronta 16, Novi Sad', NULL);
INSERT INTO kupci (korisnik_id, adresa, broj_kartice) VALUES (5, 'Narodnog fronta 19, Novi Sad', NULL);

-- Kategorije (stari 1-8 -> novi 7-14)
INSERT INTO kategorije (kategorija_id, naziv) VALUES
                                                  (7,  'Pizza'),
                                                  (8,  'Burgeri'),
                                                  (9,  'Pasta'),
                                                  (10, 'Piletina'),
                                                  (11, 'Salate'),
                                                  (12, 'Deserti'),
                                                  (13, 'Pića'),
                                                  (14, 'Ostalo');

-- Proizvodi (stari id + 16, kategorija_id + 6)
INSERT INTO proizvodi (proizvod_id, naziv, opis, cena, kolicina, merna_jedinica, kalorije, fotografija, kategorija_id) VALUES
                                                                                                                           (17, 'Margherita', 'Klasična pizza sa paradajz sosom, mocarelom i bosiljkom', 850.00, 450.00, 'g', 850.00, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=600&h=400&fit=crop&auto=format', 7),
                                                                                                                           (18, 'Quattro Formaggi', 'Pizza sa četiri vrste sira — mocarela, gorgonzola, parmezan, čeder', 980.00, 500.00, 'g', 980.00, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&h=400&fit=crop&auto=format', 7),
                                                                                                                           (19, 'Pepperoni', 'Pizza sa pikantnom pepperoni kobasicom i mocarelom', 1050.00, 480.00, 'g', 1050.00, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=600&h=400&fit=crop&auto=format', 7),
                                                                                                                           (20, 'Vegetariana', 'Pizza sa povrćem — paprika, tikvice, pečurke, masline', 780.00, 520.00, 'g', 780.00, 'https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?w=600&h=400&fit=crop&auto=format', 7),
                                                                                                                           (21, 'Classic Burger', 'Goveđi burger sa salatom, paradajzom, krastavcem i kečapom', 750.00, 300.00, 'g', 750.00, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&h=400&fit=crop&auto=format', 8),
                                                                                                                           (22, 'BBQ Bacon Burger', 'Dupli burger sa slaninom, čedar sirom i BBQ sosom', 950.00, 400.00, 'g', 950.00, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=600&h=400&fit=crop&auto=format', 8),
                                                                                                                           (23, 'Crispy Chicken Burger', 'Pohana piletina, coleslaw salata i majonez', 820.00, 350.00, 'g', 820.00, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=600&h=400&fit=crop&auto=format', 8),
                                                                                                                           (24, 'Veggie Burger', 'Burger od sočiva sa avokadom i rukola salatom', 680.00, 280.00, 'g', 680.00, 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=600&h=400&fit=crop&auto=format', 8),
                                                                                                                           (25, 'Spaghetti Bolognese', 'Špagete sa klasičnim bolonjez sosom od mlevenog mesa', 720.00, 400.00, 'g', 720.00, 'https://images.unsplash.com/photo-1551892374-ecf8754cf8b0?w=600&h=400&fit=crop&auto=format', 9),
                                                                                                                           (26, 'Penne Arrabiata', 'Pene sa pikantnim paradajz sosom i belim lukom', 650.00, 350.00, 'g', 650.00, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=600&h=400&fit=crop&auto=format', 9),
                                                                                                                           (27, 'Carbonara', 'Špagete sa kremastim sosom od jaja, pančete i parmezana', 880.00, 420.00, 'g', 880.00, 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600&h=400&fit=crop&auto=format', 9),
                                                                                                                           (28, 'Pasta Pesto', 'Pasta sa domaćim pesto sosom od bosiljka i pinjola', 710.00, 380.00, 'g', 710.00, 'https://images.unsplash.com/photo-1473093226795-af9932fe5856?w=600&h=400&fit=crop&auto=format', 9),
                                                                                                                           (29, 'Chicken Wings', 'Hrskava piletina u pikantnom sosu, 8 komada', 620.00, 280.00, 'g', 620.00, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=600&h=400&fit=crop&auto=format', 10),
                                                                                                                           (30, 'Grilled Chicken', 'Grilovana piletina sa povrćem i tzatziki sosom', 540.00, 450.00, 'g', 540.00, 'https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=600&h=400&fit=crop&auto=format', 10),
                                                                                                                           (31, 'Chicken Nuggets', 'Hrskavi nuggets od pilećeg filea, 10 komada', 580.00, 200.00, 'g', 580.00, 'https://images.unsplash.com/photo-1562967914-608f82629710?w=600&h=400&fit=crop&auto=format', 10),
                                                                                                                           (32, 'Butter Chicken', 'Kremasti indijski kari sa piletinom i basmati pirinčem', 760.00, 450.00, 'g', 760.00, 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=600&h=400&fit=crop&auto=format', 10),
                                                                                                                           (33, 'Caesar Salata', 'Rimska salata, pileće meso, krutoni, parmezan i Caesar dresing', 480.00, 350.00, 'g', 480.00, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=600&h=400&fit=crop&auto=format', 11),
                                                                                                                           (34, 'Grčka Salata', 'Paradajz, krastavac, feta sir, masline i origano', 380.00, 300.00, 'g', 380.00, 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=600&h=400&fit=crop&auto=format', 11),
                                                                                                                           (35, 'Caprese', 'Mocarela, paradajz, bosiljak i maslinovo ulje', 420.00, 280.00, 'g', 420.00, 'https://images.unsplash.com/photo-1592417817098-8fd3d9eb14a5?w=600&h=400&fit=crop&auto=format', 11),
                                                                                                                           (36, 'Tiramisu', 'Klasični italijanski desert sa mascarpone kremom i kafom', 450.00, 150.00, 'g', 450.00, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600&h=400&fit=crop&auto=format', 12),
                                                                                                                           (37, 'Cheesecake', 'Njujorški cheesecake sa šumskim voćem', 520.00, 180.00, 'g', 520.00, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=600&h=400&fit=crop&auto=format', 12),
                                                                                                                           (38, 'Čokoladna torta', 'Bogata čokoladna torta sa ganache prelivom', 580.00, 150.00, 'g', 580.00, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&h=400&fit=crop&auto=format', 12),
                                                                                                                           (39, 'Palačinke', 'Domaće palačinke sa Nutellom i bananama', 490.00, 160.00, 'g', 490.00, 'https://images.unsplash.com/photo-1519676867240-f03562e64548?w=600&h=400&fit=crop&auto=format', 12),
                                                                                                                           (40, 'Coca-Cola', 'Osvežavajuća Coca-Cola, 330ml', 139.00, 330.00, 'ml', 139.00, 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=600&h=400&fit=crop&auto=format', 13),
                                                                                                                           (41, 'Pepsi', 'Ledena Pepsi, 330ml', 135.00, 330.00, 'ml', 135.00, 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e?w=600&h=400&fit=crop&auto=format', 13),
                                                                                                                           (42, 'Limunada', 'Domaća limunada sa mentom i ledom, 500ml', 120.00, 500.00, 'ml', 120.00, 'https://bellyfull.net/wp-content/uploads/2022/05/Lemonade-blog-2.jpg', 13),
                                                                                                                           (43, 'Sok od narandže', 'Sveže ceđeni sok od narandže, 400ml', 180.00, 400.00, 'ml', 180.00, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=600&h=400&fit=crop&auto=format', 13),
                                                                                                                           (44, 'Voda', 'Negazirana mineralna voda, 500ml', 0.00, 500.00, 'ml', 0.00, 'https://i.pinimg.com/originals/1f/43/e2/1f43e25c47358dfcd505bc39913c60fe.jpg', 13),
                                                                                                                           (45, 'Riblja čorba', 'Tradicionalna riblja čorba', 310.00, 330.00, 'g', 310.00, 'https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=600&h=400&fit=crop&auto=format', 14),
                                                                                                                           (46, 'Takosi', 'Meksički takosi sa mesom i povrćem, 3 komada', 620.00, 300.00, 'g', 620.00, 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=600&h=400&fit=crop&auto=format', 14),
                                                                                                                           (47, 'Proja sa sirom', 'Domaća proja sa sitnim sirom', 260.00, 120.00, 'g', 260.00, 'https://png.pngtree.com/png-vector/20241102/ourmid/pngtree-classic-homemade-cornbread-slice-with-a-golden-crust-on-white-background-png-image_14232559.png', 14),
                                                                                                                           (48, 'Mešano meso sa roštilja', 'Bogata porcija mešanog mesa sa roštilja', 890.00, 600.00, 'g', 890.00, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600&h=400&fit=crop&auto=format', 14);

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

-- Stavke porudzbine (porudzbina_id: 1, stavke_menija: 17-23)
INSERT INTO stavke_porudzbine (stavka_id, cena, porudzbina_id, kolicina, stavka_menija_id) VALUES
                                                                                          (1, 9.80, 1, 1, 17),
                                                                                          (2, 9.80, 1, 2, 18),
                                                                                          (3, 9.80, 1, 3, 19),
                                                                                          (4, 9.80, 1, 4, 20),
                                                                                          (5, 9.80, 1, 5, 21),
                                                                                          (6, 9.80, 1, 6, 22),
                                                                                          (7, 9.80, 1, 7, 23);

-- Klikovi (korisnik_id: 2->5, proizvod_id: stari+16)
-- Napomena: uklonjena sintaksna greška u originalu (INSERT INTO resignation)
INSERT INTO klikovi (klik_id, tip_akcije, vreme_klika, korisnik_id, proizvod_id) VALUES
                                                                                     (1,  'PREGLED',          '2026-05-24 11:18:19.77089',   5, 21),
                                                                                     (2,  'PREGLED',          '2026-05-24 11:18:20.970097',  5, 21),
                                                                                     (3,  'PREGLED',          '2026-05-24 11:18:21.96425',   5, 21),
                                                                                     (4,  'PREGLED',          '2026-05-24 11:18:23.339181',  5, 25),
                                                                                     (5,  'PREGLED',          '2026-05-24 11:18:23.587001',  5, 25),
                                                                                     (6,  'PREGLED',          '2026-05-24 11:20:37.208674',  5, 27),
                                                                                     (7,  'PREGLED',          '2026-05-24 12:21:14.605071',  5, 30),
                                                                                     (8,  'PREGLED',          '2026-05-24 12:21:15.745218',  5, 30),
                                                                                     (9,  'PREGLED',          '2026-05-24 13:09:53.582406',  5, 39),
                                                                                     (10, 'PREGLED',          '2026-05-24 13:13:51.795557',  5, 48),
                                                                                     (11, 'PREGLED',          '2026-05-24 13:13:52.990959',  5, 48),
                                                                                     (12, 'PREGLED',          '2026-05-24 13:15:06.971218',  5, 48),
                                                                                     (13, 'PREGLED',          '2026-05-24 13:19:02.19047',   5, 47),
                                                                                     (14, 'PREGLED',          '2026-05-24 13:19:04.136781',  5, 47),
                                                                                     (15, 'PREGLED',          '2026-05-24 13:20:56.377343',  5, 44),
                                                                                     (16, 'PREGLED',          '2026-05-24 13:21:36.645282',  5, 42),
                                                                                     (17, 'PREGLED',          '2026-05-24 13:22:46.494394',  5, 44),
                                                                                     (18, 'PREGLED',          '2026-05-24 13:46:10.569618',  5, 18),
                                                                                     (19, 'PREGLED',          '2026-05-24 18:22:09.433661',  5, 42),
                                                                                     (20, 'KORPA',            '2026-05-24 18:22:20.19602',   5, 42),
                                                                                     (21, 'KORPA',            '2026-05-24 18:22:31.277277',  5, 42),
                                                                                     (22, 'PREGLED',          '2026-05-24 18:22:49.912077',  5, 17),
                                                                                     (23, 'KORPA',            '2026-05-25 09:07:22.400348',  5, 18),
                                                                                     (24, 'PREGLED',          '2026-05-25 09:12:20.191611',  5, 27),
                                                                                     (25, 'KORPA',            '2026-05-25 09:12:49.583981',  5, 29),
                                                                                     (26, 'KORPA',            '2026-05-25 09:12:53.837157',  5, 29),
                                                                                     (27, 'PREGLED',          '2026-05-25 09:18:08.261056',  5, 17),
                                                                                     (28, 'KORPA',            '2026-05-25 09:45:03.390327',  5, 18),
                                                                                     (29, 'KORPA',            '2026-05-25 09:59:46.08506',   5, 42),
                                                                                     (30, 'KORPA',            '2026-05-25 10:00:06.103191',  5, 18),
                                                                                     (31, 'KORPA',            '2026-05-25 10:17:53.926342',  5, 42),
                                                                                     (32, 'KORPA',            '2026-05-25 10:23:38.663827',  5, 18),
                                                                                     (33, 'PREGLED',          '2026-05-25 10:24:28.563324',  5, 42),
                                                                                     (34, 'KORPA',            '2026-05-25 18:27:16.045984',  5, 18),
                                                                                     (35, 'PLACANJE',         '2026-05-25 18:27:18.820107',  5, 18),
                                                                                     (36, 'KUPOVINA',         '2026-05-25 18:27:18.830747',  5, 18),
                                                                                     (37, 'PREGLED',          '2026-05-25 18:35:28.70401',   5, 30),
                                                                                     (38, 'PREGLED',          '2026-05-25 18:45:38.15342',   5, 18),
                                                                                     (39, 'DODAJ_OMILJENI',   '2026-05-25 23:36:50.74754',   5, 18),
                                                                                     (40, 'UKLONI_OMILJENI',  '2026-05-25 23:36:51.352762',  5, 18),
                                                                                     (41, 'KORPA',            '2026-05-25 23:36:55.221134',  5, 18),
                                                                                     (42, 'UKLONI_IZ_KORPE',  '2026-05-25 23:37:01.971029',  5, 18),
                                                                                     (43, 'REZULTAT_PRETRAGE','2026-05-25 23:38:32.146358',  5, 42),
                                                                                     (44, 'DODAJ_OMILJENI',   '2026-05-25 23:53:18.022442',  5, 20),
                                                                                     (45, 'UKLONI_OMILJENI',  '2026-05-25 23:53:18.753286',  5, 20),
                                                                                     (46, 'DETALJI',          '2026-05-25 23:53:19.440917',  5, 20),
                                                                                     (47, 'KORPA',            '2026-05-25 23:53:24.247347',  5, 20),
                                                                                     (48, 'UKLONI_IZ_KORPE',  '2026-05-25 23:53:27.653239',  5, 20);

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


-- Sinhronizacija sekvenci za bazu
SELECT setval(pg_get_serial_sequence('korisnici', 'korisnik_id'), MAX(korisnik_id)) FROM korisnici;
SELECT setval(pg_get_serial_sequence('kategorije', 'kategorija_id'), MAX(kategorija_id)) FROM kategorije;
SELECT setval(pg_get_serial_sequence('alergeni', 'alergen_id'), MAX(alergen_id)) FROM alergeni;
SELECT setval(pg_get_serial_sequence('sastojci', 'sastojak_id'), MAX(sastojak_id)) FROM sastojci;
SELECT setval(pg_get_serial_sequence('proizvodi', 'proizvod_id'), MAX(proizvod_id)) FROM proizvodi;
SELECT setval(pg_get_serial_sequence('meniji', 'meni_id'), MAX(meni_id)) FROM meniji;
SELECT setval(pg_get_serial_sequence('stavke_menija', 'stavka_id'), MAX(stavka_id)) FROM stavke_menija;
SELECT setval(pg_get_serial_sequence('stavke_menija', 'stavka_id'), MAX(stavka_id)) FROM stavke_menija;
SELECT setval(pg_get_serial_sequence('kuponi', 'kupon_id'), MAX(kupon_id)) FROM kuponi;
SELECT setval(pg_get_serial_sequence('porudzbine', 'porudzbina_id'), MAX(porudzbina_id)) FROM porudzbine;
SELECT setval(pg_get_serial_sequence('statusi_porudzbine', 'status_istorija_id'), MAX(status_istorija_id)) FROM statusi_porudzbine;