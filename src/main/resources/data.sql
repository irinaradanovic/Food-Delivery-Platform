-- =========================================================================
-- data.sql — kompletan seed fajl
-- Redosled je bitan: FK mora postojati pre nego sto se referencira
-- =========================================================================

-- -------------------------------------------------------------------------
-- 1. KORISNICI — menadžeri (ID 1, 2, 3)
-- Hibernate generise kolone camelCase -> snake_case:
--   korisnikId -> korisnik_id, datumReg -> datum_reg, itd.
-- -------------------------------------------------------------------------
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES
                                                                                                 (1, 'Marko',  'Marković', '064123456', 'password123', 'menadzer1@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (2, 'Nikola', 'Nikolić',  '065987654', 'password123', 'menadzer2@test.com', '2026-05-25', 'MENADZER'),
                                                                                                 (3, 'Jovana', 'Jovanić',  '063111222', 'password123', 'menadzer3@test.com', '2026-05-25', 'MENADZER');

-- -------------------------------------------------------------------------
-- 2. MENADŽERI — JOINED tabela, PK je korisnikId (-> korisnik_id)
-- -------------------------------------------------------------------------
INSERT INTO menadzeri (korisnik_id) VALUES (1), (2), (3);

-- -------------------------------------------------------------------------
-- 3. RESTORANI — FK na menadzeri (menadzerId -> menadzer_id)
-- -------------------------------------------------------------------------
INSERT INTO restorani (restoran_id, naziv, adresa, kontakt, menadzer_id) VALUES
                                                                             (1, 'Big Bite Centar',       'Trg Slobode 4, Novi Sad',              '021-555-333', 1),
                                                                             (2, 'Pizzeria Napoli',       'Bulevar Oslobodjenja 12, Novi Sad',    '021-444-555', 2),
                                                                             (3, 'Green Garden Healthy',  'Fruškogorska 8, Novi Sad',             '021-666-777', 3);

-- -------------------------------------------------------------------------
-- 4. KATEGORIJE (jedan jedini set, ID 1-8)
-- -------------------------------------------------------------------------
INSERT INTO kategorije (kategorija_id, naziv) VALUES
                                                  (1, 'Pizza'),
                                                  (2, 'Burgeri'),
                                                  (3, 'Pasta'),
                                                  (4, 'Piletina'),
                                                  (5, 'Salate'),
                                                  (6, 'Deserti'),
                                                  (7, 'Pića'),
                                                  (8, 'Ostalo');

-- -------------------------------------------------------------------------
-- 5. ALERGENI
-- -------------------------------------------------------------------------
INSERT INTO alergeni (alergen_id, naziv) VALUES
                                             (1, 'Gluten'),
                                             (2, 'Kikiriki'),
                                             (3, 'Laktoza'),
                                             (4, 'Jaja');

-- -------------------------------------------------------------------------
-- 6. SASTOJCI
-- -------------------------------------------------------------------------
INSERT INTO sastojci (sastojak_id, naziv) VALUES
                                              (1, 'Govedina'),
                                              (2, 'Mocarela'),
                                              (3, 'Pelat'),
                                              (4, 'Cezar sos'),
                                              (5, 'Zelena salata'),
                                              (6, 'Čedar sir');

-- -------------------------------------------------------------------------
-- 7. KORISNICI — kupci (ID 4, 5 — ne kolizuju sa menadžerima)
-- -------------------------------------------------------------------------
INSERT INTO korisnici (korisnik_id, ime, prezime, telefon, lozinka, email, datum_reg, uloga) VALUES
                                                                                                 (4, 'Marko', 'Markovic', '064235768',   '123456', 'markovic@gmail.com', '2026-05-25', 'KUPAC'),
                                                                                                 (5, 'Nenad', 'Lalic',    '06789256812', '123456', 'lalic@gmail.com',    '2026-05-25', 'KUPAC');

-- -------------------------------------------------------------------------
-- 8. KUPCI — JOINED tabela, PK je korisnikId (-> korisnik_id)
--    Hibernate: brojKartice -> broj_kartice
-- -------------------------------------------------------------------------
INSERT INTO kupci (korisnik_id, adresa, broj_kartice) VALUES
                                                          (4, 'Narodnog fronta 16, Novi Sad', NULL),
                                                          (5, 'Narodnog fronta 19, Novi Sad', NULL);

-- -------------------------------------------------------------------------
-- 9. PROIZVODI — FK na kategorije (kategorijaId -> kategorija_id)
--    Hibernate kolone: proizvodId, naziv, opis, kalorije, cena,
--    fotografija, kolicina, mernaJedinica, kategorijaId
--    -> proizvod_id, naziv, opis, kalorije, cena,
--       fotografija, kolicina, merna_jedinica, kategorija_id
-- -------------------------------------------------------------------------
INSERT INTO proizvodi (proizvod_id, naziv, opis, kalorije, cena, fotografija, kolicina, merna_jedinica, kategorija_id) VALUES
                                                                                                                           (1,  'Margherita',           'Klasična pizza sa paradajz sosom, mocarelom i bosiljkom',         850.00, 850.00, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=600&h=400&fit=crop&auto=format', 450.00, 'g', 1),
                                                                                                                           (2,  'Quattro Formaggi',     'Pizza sa četiri vrste sira — mocarela, gorgonzola, parmezan, čeder', 980.00, 980.00, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&h=400&fit=crop&auto=format', 500.00, 'g', 1),
                                                                                                                           (3,  'Pepperoni',            'Pizza sa pikantnom pepperoni kobasicom i mocarelom',              1050.00, 1050.00,'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=600&h=400&fit=crop&auto=format', 480.00, 'g', 1),
                                                                                                                           (4,  'Vegetariana',          'Pizza sa povrćem — paprika, tikvice, pečurke, masline',            780.00, 780.00, 'https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?w=600&h=400&fit=crop&auto=format', 520.00, 'g', 1),
                                                                                                                           (5,  'Classic Burger',       'Goveđi burger sa salatom, paradajzom, krastavcem i kečapom',      750.00, 750.00, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&h=400&fit=crop&auto=format', 300.00, 'g', 2),
                                                                                                                           (6,  'BBQ Bacon Burger',     'Dupli burger sa slaninom, čedar sirom i BBQ sosom',                950.00, 950.00, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=600&h=400&fit=crop&auto=format', 400.00, 'g', 2),
                                                                                                                           (7,  'Crispy Chicken Burger','Pohana piletina, coleslaw salata i majonez',                       820.00, 820.00, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=600&h=400&fit=crop&auto=format', 350.00, 'g', 2),
                                                                                                                           (8,  'Veggie Burger',        'Burger od sočiva sa avokadom i rukola salatom',                    680.00, 680.00, 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=600&h=400&fit=crop&auto=format', 280.00, 'g', 2),
                                                                                                                           (9,  'Spaghetti Bolognese',  'Špagete sa klasičnim bolonjez sosom od mlevenog mesa',             720.00, 720.00, 'https://images.unsplash.com/photo-1551892374-ecf8754cf8b0?w=600&h=400&fit=crop&auto=format', 400.00, 'g', 3),
                                                                                                                           (10, 'Penne Arrabiata',      'Pene sa pikantnim paradajz sosom i belim lukom',                   650.00, 650.00, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=600&h=400&fit=crop&auto=format', 350.00, 'g', 3),
                                                                                                                           (11, 'Carbonara',            'Špagete sa kremastim sosom od jaja, pančete i parmezana',          880.00, 880.00, 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600&h=400&fit=crop&auto=format', 420.00, 'g', 3),
                                                                                                                           (12, 'Pasta Pesto',          'Pasta sa domaćim pesto sosom od bosiljka i pinjola',               710.00, 710.00, 'https://images.unsplash.com/photo-1473093226795-af9932fe5856?w=600&h=400&fit=crop&auto=format', 380.00, 'g', 3),
                                                                                                                           (13, 'Chicken Wings',        'Hrskava piletina u pikantnom sosu, 8 komada',                      620.00, 620.00, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=600&h=400&fit=crop&auto=format', 280.00, 'g', 4),
                                                                                                                           (14, 'Grilled Chicken',      'Grilovana piletina sa povrćem i tzatziki sosom',                   540.00, 540.00, 'https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=600&h=400&fit=crop&auto=format', 450.00, 'g', 4),
                                                                                                                           (15, 'Chicken Nuggets',      'Hrskavi nuggets od pilećeg filea, 10 komada',                      580.00, 580.00, 'https://images.unsplash.com/photo-1562967914-608f82629710?w=600&h=400&fit=crop&auto=format', 200.00, 'g', 4),
                                                                                                                           (16, 'Butter Chicken',       'Kremasti indijski kari sa piletinom i basmati pirinčem',           760.00, 760.00, 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=600&h=400&fit=crop&auto=format', 450.00, 'g', 4),
                                                                                                                           (17, 'Caesar Salata',        'Rimska salata, pileće meso, krutoni, parmezan i Caesar dresing',   480.00, 480.00, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=600&h=400&fit=crop&auto=format', 350.00, 'g', 5),
                                                                                                                           (18, 'Grčka Salata',         'Paradajz, krastavac, feta sir, masline i origano',                 380.00, 380.00, 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=600&h=400&fit=crop&auto=format', 300.00, 'g', 5),
                                                                                                                           (19, 'Caprese',              'Mocarela, paradajz, bosiljak i maslinovo ulje',                    420.00, 420.00, 'https://images.unsplash.com/photo-1592417817098-8fd3d9eb14a5?w=600&h=400&fit=crop&auto=format', 280.00, 'g', 5),
                                                                                                                           (20, 'Tiramisu',             'Klasični italijanski desert sa mascarpone kremom i kafom',          450.00, 450.00, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600&h=400&fit=crop&auto=format', 150.00, 'g', 6),
                                                                                                                           (21, 'Cheesecake',           'Njujorški cheesecake sa šumskim voćem',                            520.00, 520.00, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=600&h=400&fit=crop&auto=format', 180.00, 'g', 6),
                                                                                                                           (22, 'Čokoladna torta',      'Bogata čokoladna torta sa ganache prelivom',                       580.00, 580.00, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&h=400&fit=crop&auto=format', 150.00, 'g', 6),
                                                                                                                           (23, 'Palačinke',            'Domaće palačinke sa Nutellom i bananama',                          490.00, 490.00, 'https://images.unsplash.com/photo-1519676867240-f03562e64548?w=600&h=400&fit=crop&auto=format', 160.00, 'g', 6),
                                                                                                                           (24, 'Coca-Cola',            'Osvežavajuća Coca-Cola, 330ml',                                    139.00, 139.00, 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=600&h=400&fit=crop&auto=format', 330.00, 'ml', 7),
                                                                                                                           (25, 'Pepsi',                'Ledena Pepsi, 330ml',                                              135.00, 135.00, 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e?w=600&h=400&fit=crop&auto=format', 330.00, 'ml', 7),
                                                                                                                           (26, 'Limunada',             'Domaća limunada sa mentom i ledom, 500ml',                         120.00, 120.00, 'https://bellyfull.net/wp-content/uploads/2022/05/Lemonade-blog-2.jpg',                          500.00, 'ml', 7),
                                                                                                                           (27, 'Sok od narandže',      'Sveže ceđeni sok od narandže, 400ml',                              180.00, 180.00, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=600&h=400&fit=crop&auto=format', 400.00, 'ml', 7),
                                                                                                                           (28, 'Voda',                 'Negazirana mineralna voda, 500ml',                                    0.00,   0.00, 'https://i.pinimg.com/originals/1f/43/e2/1f43e25c47358dfcd505bc39913c60fe.jpg',                  500.00, 'ml', 7),
                                                                                                                           (29, 'Riblja čorba',         'Tradicionalna riblja čorba',                                       310.00, 310.00, 'https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=600&h=400&fit=crop&auto=format', 330.00, 'g', 8),
                                                                                                                           (30, 'Takosi',               'Meksički takosi sa mesom i povrćem, 3 komada',                     620.00, 620.00, 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=600&h=400&fit=crop&auto=format', 300.00, 'g', 8),
                                                                                                                           (31, 'Proja sa sirom',       'Domaća proja sa sitnim sirom',                                     260.00, 260.00, 'https://png.pngtree.com/png-vector/20241102/ourmid/pngtree-classic-homemade-cornbread-slice-with-a-golden-crust-on-white-background-png-image_14232559.png', 120.00, 'g', 8),
                                                                                                                           (32, 'Mešano meso sa roštilja','Bogata porcija mešanog mesa sa roštilja',                        890.00, 890.00, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600&h=400&fit=crop&auto=format', 600.00, 'g', 8);

-- -------------------------------------------------------------------------
-- 10. PROIZVOD — ALERGENI (join tabela)
--     Hibernate: proizvodId -> proizvod_id, alergenId -> alergen_id
-- -------------------------------------------------------------------------
INSERT INTO proizvod_alergeni (proizvod_id, alergen_id) VALUES
                                                            (1,  1), (1,  3),
                                                            (2,  1), (2,  3),
                                                            (3,  1), (3,  3),
                                                            (4,  1),
                                                            (5,  1), (5,  3),
                                                            (6,  1), (6,  3),
                                                            (7,  1), (7,  4),
                                                            (9,  1),
                                                            (11, 1), (11, 4),
                                                            (17, 1), (17, 4),
                                                            (20, 4), (20, 3),
                                                            (21, 4), (21, 3),
                                                            (22, 4), (22, 3);

-- -------------------------------------------------------------------------
-- 11. PROIZVOD — SASTOJCI (join tabela)
--     Hibernate: proizvodId -> proizvod_id, sastojakId -> sastojak_id
-- -------------------------------------------------------------------------
INSERT INTO proizvod_sastojci (proizvod_id, sastojak_id) VALUES
                                                             (1,  2), (1,  3),
                                                             (2,  2), (2,  3),
                                                             (3,  2), (3,  3),
                                                             (5,  1), (5,  6),
                                                             (6,  1), (6,  6),
                                                             (17, 4), (17, 5);

-- -------------------------------------------------------------------------
-- 12. MENIJI — SINGLE_TABLE, discriminator kolona: tipMenija -> tip_menija
--     Hibernate: meniId, restoranId, datumOd, datumDo -> meni_id,
--     restoran_id, datum_od, datum_do
--     SezonskiMeni: pocetakSezone, krajSezone -> pocetak_sezone, kraj_sezone
--     VremenskiMeni: vremeOd, vremeDo -> vreme_od, vreme_do
-- -------------------------------------------------------------------------

-- Restoran 1 — STANDARDNI meni
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, restoran_id) VALUES
    (1, 'Glavni Meni Big Bite', 'Standardna ponuda hrane i pića', 'STANDARDNI', 'v1', true, '2026-05-26', 1);

-- Restoran 2 — VREMENSKI meni (doručak 08:00-11:00)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, vreme_od, vreme_do, restoran_id) VALUES
    (2, 'Jutarnji Meni', 'Ponuda za doručak', 'VREMENSKI', 'v1', true, '2026-05-26', '08:00:00', '11:00:00', 2);

-- Restoran 3 — SEZONSKI meni (letnji)
INSERT INTO meniji (meni_id, naziv, opis, tip_menija, verzija, aktivan, datum_od, pocetak_sezone, kraj_sezone, restoran_id) VALUES
    (3, 'Letnji Meni 2026', 'Laka letnja osveženja', 'SEZONSKI', 'v1', true, '2026-05-26', '2026-06-01', '2026-08-31', 3);

-- -------------------------------------------------------------------------
-- 13. STAVKE MENIJA — posle menija I posle proizvoda
--     Hibernate: stavkaId, meniId, proizvodId, vremePripremeMin,
--     vremePripremeMax -> stavka_id, meni_id, proizvod_id,
--     vreme_pripreme_min, vreme_pripreme_max
-- -------------------------------------------------------------------------
INSERT INTO stavke_menija (stavka_id, meni_id, proizvod_id, cena, dostupno, vreme_pripreme_min, vreme_pripreme_max) VALUES
                                                                                                                        (1, 1,  5, 750.00, true, 10, 15),  -- Classic Burger u Restoran 1
                                                                                                                        (2, 1,  6, 950.00, true, 12, 18),  -- BBQ Bacon Burger u Restoran 1
                                                                                                                        (3, 1, 24, 139.00, true,  1,  2),  -- Coca-Cola u Restoran 1
                                                                                                                        (4, 2,  1, 850.00, true, 12, 20),  -- Margherita u Restoran 2
                                                                                                                        (5, 2,  2, 980.00, true, 15, 22),  -- Quattro Formaggi u Restoran 2
                                                                                                                        (6, 3, 17, 480.00, true,  5, 10),  -- Caesar Salata u Restoran 3
                                                                                                                        (7, 3, 18, 380.00, true,  5,  8);  -- Grčka Salata u Restoran 3

-- -------------------------------------------------------------------------
-- 14. OMILJENE KATEGORIJE
--     Hibernate: omiljenaKategorijaId, korisnik_id, kategorija_id,
--     datumDodavanja -> omiljena_kategorija_id, datum_dodavanja
--     Kupci su ID 4 i 5!
-- -------------------------------------------------------------------------
INSERT INTO omiljene_kategorije (omiljena_kategorija_id, datum_dodavanja, kategorija_id, korisnik_id) VALUES
                                                                                                          (1, '2026-05-24 12:19:57.447502', 3, 4),
                                                                                                          (2, '2026-05-24 12:32:55.337159', 5, 4);

-- -------------------------------------------------------------------------
-- 15. OMILJENI PROIZVODI
--     Hibernate: omiljeniId, korisnikId, proizvodId, datumDodavanja
--     -> omiljeni_id, korisnik_id, proizvod_id, datum_dodavanja
--     Kupci su ID 4 i 5!
-- -------------------------------------------------------------------------
INSERT INTO omiljeni_proizvodi (omiljeni_id, datum_dodavanja, korisnik_id, proizvod_id) VALUES
                                                                                            (1, '2026-05-24 11:27:48.164161', 4,  9),
                                                                                            (2, '2026-05-25 10:24:59.061989', 4, 25);

-- -------------------------------------------------------------------------
-- 16. SEQUENCE RESET — da auto-increment ne napravi konflikt pri novim
--     INSERT-ima kroz aplikaciju
-- -------------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('korisnici',          'korisnik_id'),          MAX(korisnik_id))          FROM korisnici;
SELECT setval(pg_get_serial_sequence('restorani',          'restoran_id'),          MAX(restoran_id))          FROM restorani;
SELECT setval(pg_get_serial_sequence('kategorije',         'kategorija_id'),        MAX(kategorija_id))        FROM kategorije;
SELECT setval(pg_get_serial_sequence('alergeni',           'alergen_id'),           MAX(alergen_id))           FROM alergeni;
SELECT setval(pg_get_serial_sequence('sastojci',           'sastojak_id'),          MAX(sastojak_id))          FROM sastojci;
SELECT setval(pg_get_serial_sequence('proizvodi',          'proizvod_id'),          MAX(proizvod_id))          FROM proizvodi;
SELECT setval(pg_get_serial_sequence('meniji',             'meni_id'),              MAX(meni_id))              FROM meniji;
SELECT setval(pg_get_serial_sequence('stavke_menija',      'stavka_id'),            MAX(stavka_id))            FROM stavke_menija;
SELECT setval(pg_get_serial_sequence('omiljene_kategorije','omiljena_kategorija_id'),MAX(omiljena_kategorija_id)) FROM omiljene_kategorije;
SELECT setval(pg_get_serial_sequence('omiljeni_proizvodi', 'omiljeni_id'),          MAX(omiljeni_id))          FROM omiljeni_proizvodi;