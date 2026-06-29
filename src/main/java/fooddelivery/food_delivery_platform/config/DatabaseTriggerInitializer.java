package fooddelivery.food_delivery_platform.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseTriggerInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeTriggers() {
        initVerzionisanjeMenua();
        initPreporukePodsistem();
    }

    // -------------------------------------------------------------------------
    // Trigger: verzionisanje menija
    // -------------------------------------------------------------------------
    private void initVerzionisanjeMenua() {
        String sql = """
            DROP TRIGGER IF EXISTS trg_nakon_kreiranja_verzije ON meniji;

            CREATE OR REPLACE FUNCTION trg_verzionisanje_menija_insert()
            RETURNS TRIGGER AS $$
            DECLARE
                v_stari_meni_id INT;
            BEGIN
                SELECT meni_id INTO v_stari_meni_id
                FROM meniji
                WHERE grupni_meni_id = NEW.grupni_meni_id
                  AND aktivan = TRUE
                  AND meni_id != NEW.meni_id
                LIMIT 1;

                IF v_stari_meni_id IS NULL THEN
                    RETURN NEW;
                END IF;

                UPDATE meniji
                SET aktivan = FALSE, datum_do = CURRENT_DATE
                WHERE meni_id = v_stari_meni_id;

                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql;

            CREATE TRIGGER trg_nakon_kreiranja_verzije
                AFTER INSERT ON meniji
                FOR EACH ROW
                EXECUTE FUNCTION trg_verzionisanje_menija_insert();
            """;

        entityManager.createNativeQuery(sql).executeUpdate();
    }

    // -------------------------------------------------------------------------
    // Podsistem za preporuke: schema, indeksi, funkcija, trigger
    // -------------------------------------------------------------------------
    private void initPreporukePodsistem() {
        initPreporukeSchema();
        initPreporukeIndeksi();
        initPreporukeFunkcija();
        initPreporukeTrigger();
    }

    private void initPreporukeSchema() {
        String sql = """
            DROP SCHEMA IF EXISTS preporuke CASCADE;
            CREATE SCHEMA preporuke;
            """;
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    private void initPreporukeIndeksi() {
        String sql = """
            CREATE INDEX IF NOT EXISTS idx_klikovi_kupac
                ON klikovi(korisnik_id);

            CREATE INDEX IF NOT EXISTS idx_proizvodi_kategorija
                ON proizvodi(kategorija_id);
            """;
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    private void initPreporukeFunkcija() {
        // executeUpdate() ne može da izvrši više DDL iskaza odjednom kada sadrže
        // $$ dollar-quoting — svaki CREATE FUNCTION mora biti poseban poziv.
        String sql = """
            CREATE OR REPLACE FUNCTION preporuke.personalizovane(
                p_kupac_id  BIGINT,
                p_limit     INT DEFAULT 5
            )
            RETURNS TABLE (
                out_stavka_id   BIGINT,
                out_naziv       VARCHAR,
                out_kategorija  VARCHAR,
                out_skor        NUMERIC
            )
            LANGUAGE plpgsql
            AS $func$
            DECLARE
                cur_naručeno CURSOR FOR
                    SELECT DISTINCT pr.proizvod_id,
                                    k.kategorija_id,
                                    COUNT(*) AS br_narudzbina
                    FROM   porudzbine p
                    JOIN   stavke_porudzbine sp ON sp.porudzbina_id = p.porudzbina_id
                    JOIN   stavke_menija     sm ON sm.stavka_id     = sp.stavka_menija_id
                    JOIN   proizvodi         pr ON pr.proizvod_id   = sm.proizvod_id
                    LEFT JOIN kategorije      k ON k.kategorija_id  = pr.kategorija_id
                    WHERE  p.korisnik_id = p_kupac_id
                    GROUP  BY pr.proizvod_id, k.kategorija_id;

                cur_kandidati CURSOR (v_kategorija_id BIGINT) FOR
                    SELECT sm.stavka_id        AS kan_stavka_id,
                           pr.naziv            AS kan_naziv,
                           k.naziv             AS kan_kategorija
                    FROM   stavke_menija sm
                    JOIN   meniji        m  ON m.meni_id       = sm.meni_id
                    JOIN   proizvodi     pr ON pr.proizvod_id  = sm.proizvod_id
                    LEFT JOIN kategorije k  ON k.kategorija_id = pr.kategorija_id
                    WHERE  m.aktivan       = TRUE
                      AND  sm.dostupno     = TRUE
                      AND  sm.obrisan      = FALSE
                      AND  k.kategorija_id = v_kategorija_id
                      AND  pr.proizvod_id NOT IN (
                            SELECT DISTINCT pr2.proizvod_id
                            FROM   porudzbine p2
                            JOIN   stavke_porudzbine sp2 ON sp2.porudzbina_id = p2.porudzbina_id
                            JOIN   stavke_menija     sm2 ON sm2.stavka_id     = sp2.stavka_menija_id
                            JOIN   proizvodi         pr2 ON pr2.proizvod_id   = sm2.proizvod_id
                            WHERE  p2.korisnik_id = p_kupac_id
                      );

                rec_naručeno  RECORD;
                rec_kandidat  RECORD;
                v_skor        NUMERIC;
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM korisnici WHERE korisnik_id = p_kupac_id AND uloga = 'KUPAC'
                ) THEN
                    RAISE EXCEPTION 'Kupac ID=% ne postoji', p_kupac_id;
                END IF;

                IF p_limit <= 0 THEN
                    RAISE EXCEPTION 'Limit mora biti pozitivan broj, dobijeno: %', p_limit;
                END IF;

                CREATE TEMP TABLE IF NOT EXISTS tmp_skorovi (
                    tmp_stavka_id  BIGINT PRIMARY KEY,
                    tmp_naziv      VARCHAR(200),
                    tmp_kategorija VARCHAR(100),
                    tmp_skor       NUMERIC DEFAULT 0
                ) ON COMMIT DELETE ROWS;

                TRUNCATE tmp_skorovi;

                OPEN cur_naručeno;
                LOOP
                    FETCH cur_naručeno INTO rec_naručeno;
                    EXIT WHEN NOT FOUND;

                    v_skor := rec_naručeno.br_narudzbina * 10.0;

                    OPEN cur_kandidati(rec_naručeno.kategorija_id);
                    LOOP
                        FETCH cur_kandidati INTO rec_kandidat;
                        EXIT WHEN NOT FOUND;

                        INSERT INTO tmp_skorovi (tmp_stavka_id, tmp_naziv, tmp_kategorija, tmp_skor)
                        VALUES (rec_kandidat.kan_stavka_id, rec_kandidat.kan_naziv, rec_kandidat.kan_kategorija, v_skor)
                        ON CONFLICT (tmp_stavka_id) DO UPDATE
                            SET tmp_skor = tmp_skorovi.tmp_skor + EXCLUDED.tmp_skor;

                    END LOOP;
                    CLOSE cur_kandidati;

                END LOOP;
                CLOSE cur_naručeno;

                IF NOT EXISTS (SELECT 1 FROM tmp_skorovi) THEN
                    INSERT INTO tmp_skorovi (tmp_stavka_id, tmp_naziv, tmp_kategorija, tmp_skor)
                    SELECT sm.stavka_id, pr.naziv, k.naziv, 0
                    FROM   stavke_menija sm
                    JOIN   meniji        m  ON m.meni_id      = sm.meni_id
                    JOIN   proizvodi     pr ON pr.proizvod_id = sm.proizvod_id
                    LEFT JOIN kategorije k  ON k.kategorija_id = pr.kategorija_id
                    WHERE  m.aktivan = TRUE AND sm.dostupno = TRUE AND sm.obrisan = FALSE
                    LIMIT  p_limit
                    ON CONFLICT DO NOTHING;
                END IF;

                RETURN QUERY
                SELECT ts.tmp_stavka_id, ts.tmp_naziv, ts.tmp_kategorija, ts.tmp_skor
                FROM   tmp_skorovi ts
                ORDER  BY ts.tmp_skor DESC
                LIMIT  p_limit;

            EXCEPTION
                WHEN OTHERS THEN
                    RAISE NOTICE 'Greška: %', SQLERRM;
                    RETURN;
            END;
            $func$;
            """;
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    private void initPreporukeTrigger() {
        String triggerFn = """
            CREATE OR REPLACE FUNCTION preporuke.trg_log_kupovine()
            RETURNS TRIGGER
            LANGUAGE plpgsql
            AS $func$
            DECLARE
                rec_stavka RECORD;
            BEGIN
                FOR rec_stavka IN (
                    SELECT sm.proizvod_id,
                           p.korisnik_id
                    FROM   stavke_porudzbine sp
                    JOIN   stavke_menija sm ON sm.stavka_id    = sp.stavka_menija_id
                    JOIN   porudzbine    p  ON p.porudzbina_id = sp.porudzbina_id
                    WHERE  sp.porudzbina_id = NEW.porudzbina_id
                ) LOOP
                    INSERT INTO klikovi (korisnik_id, proizvod_id, tip_akcije, vreme_klika)
                    VALUES (rec_stavka.korisnik_id, rec_stavka.proizvod_id, 'KUPOVINA', NOW())
                    ON CONFLICT DO NOTHING;
                END LOOP;

                RETURN NEW;

            EXCEPTION
                WHEN OTHERS THEN
                    RAISE WARNING 'Trigger greška: %', SQLERRM;
                    RETURN NEW;
            END;
            $func$;
            """;

        String triggerDdl = """
            DROP TRIGGER IF EXISTS trg_log_kupovine ON porudzbine;

            CREATE TRIGGER trg_log_kupovine
                AFTER INSERT ON porudzbine
                FOR EACH ROW
                EXECUTE FUNCTION preporuke.trg_log_kupovine();
            """;

        entityManager.createNativeQuery(triggerFn).executeUpdate();
        entityManager.createNativeQuery(triggerDdl).executeUpdate();
    }
}