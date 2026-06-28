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
        String sql = """
            CREATE INDEX IF NOT EXISTS idx_meniji_grupni_aktivan ON meniji(grupni_meni_id, aktivan);
            CREATE INDEX IF NOT EXISTS idx_stavke_menija_meni_id ON stavke_menija(meni_id);
            CREATE INDEX IF NOT EXISTS idx_proizvodi_kategorija_id ON proizvodi(kategorija_id);
            
            -- DEAKTIVIRANJE STARE VERZIJE I KOPIRANJE STAVKI U NOVU VERZIJU
            DROP TRIGGER IF EXISTS trg_nakon_kreiranja_verzije ON meniji;
                CREATE OR REPLACE FUNCTION trg_verzionisanje_menija_insert()
                RETURNS TRIGGER AS $$
 
                DECLARE
                v_stari_meni_id INT;
                v_izvor_za_kopiranje_id INT; --  promenljiva koja pamti odakle kopiramo stavke
                v_vec_uneta INT;
                v_stara_stavka RECORD;

               cur_stare_stavke CURSOR(c_stari_id INT) FOR
 
                  SELECT proizvod_id, vreme_pripreme_min, vreme_pripreme_max, cena, dostupno
                  FROM stavke_menija
                  WHERE meni_id = c_stari_id AND obrisan = FALSE;
          
                BEGIN
 
                  SELECT meni_id INTO v_stari_meni_id
                  FROM meniji
                  WHERE grupni_meni_id = NEW.grupni_meni_id
                    AND aktivan = TRUE
                    AND meni_id != NEW.meni_id
                  LIMIT 1;
 
                  IF NEW.izvorna_verzija_id IS NOT NULL THEN
                      v_izvor_za_kopiranje_id := NEW.izvorna_verzija_id;
                  ELSE
                      v_izvor_za_kopiranje_id := v_stari_meni_id;
                  END IF;

 
                  IF v_izvor_za_kopiranje_id IS NULL THEN
                      RETURN NEW;
                  END IF;

 
                  OPEN cur_stare_stavke(v_izvor_za_kopiranje_id);

                  LOOP

                      FETCH cur_stare_stavke INTO v_stara_stavka;
                      EXIT WHEN NOT FOUND;

                      SELECT COUNT(*) INTO v_vec_uneta
                      FROM stavke_menija
                      WHERE meni_id = NEW.meni_id AND proizvod_id = v_stara_stavka.proizvod_id;

                       IF v_vec_uneta = 0 THEN
                          INSERT INTO stavke_menija (
                              meni_id, proizvod_id, vreme_pripreme_min, vreme_pripreme_max, cena, dostupno, obrisan
                          ) VALUES (
                              NEW.meni_id,
                              v_stara_stavka.proizvod_id,
                              v_stara_stavka.vreme_pripreme_min,
                              v_stara_stavka.vreme_pripreme_max,
                              v_stara_stavka.cena,
                              v_stara_stavka.dostupno,
                              FALSE
                          );
                      END IF;
                  END LOOP;
                  CLOSE cur_stare_stavke;
 

                      IF v_stari_meni_id IS NOT NULL THEN
                          UPDATE meniji
                          SET aktivan = FALSE, datum_do = CURRENT_DATE
                          WHERE meni_id = v_stari_meni_id;
                      END IF;
                       RETURN NEW;

                  EXCEPTION
                      WHEN OTHERS THEN
                          RAISE EXCEPTION 'Greska u trigeru prilikom kopiranja stavki: %', SQLERRM;
                  END;
                  $$ LANGUAGE plpgsql;
            
            CREATE TRIGGER trg_nakon_kreiranja_verzije
                AFTER INSERT ON meniji
                FOR EACH ROW
                EXECUTE FUNCTION trg_verzionisanje_menija_insert();


            -- PROCENA PRIPREME JELA
            DROP FUNCTION IF EXISTS fn_analiza_vremena_pripreme(BIGINT);
            
            CREATE OR REPLACE FUNCTION fn_analiza_vremena_pripreme(
                p_kategorija_id BIGINT, 
                OUT r_min_vreme INT,
                OUT r_max_vreme INT
            ) AS $$
            DECLARE
                v_stavka_red RECORD;
                cur_stavke_kategorije CURSOR(c_kat_id BIGINT) FOR
                    SELECT sm.vreme_pripreme_min, sm.vreme_pripreme_max
                    FROM stavke_menija sm
                    JOIN proizvodi p ON sm.proizvod_id = p.proizvod_id
                    WHERE p.kategorija_id = c_kat_id AND sm.obrisan = FALSE;
                v_suma_min INT := 0;
                v_suma_max INT := 0;
                v_brojac INT := 0;
                v_postoji INT := 0;
            BEGIN
                SELECT COUNT(*) INTO v_postoji FROM kategorije WHERE kategorija_id = p_kategorija_id;
                IF v_postoji = 0 THEN
                    RAISE EXCEPTION 'Kategorija sa ID-jem % ne postoji u sistemu.', p_kategorija_id USING ERRCODE = 'P0001';
                END IF;
            
                OPEN cur_stavke_kategorije(p_kategorija_id);
                LOOP
                    FETCH cur_stavke_kategorije INTO v_stavka_red;
                    EXIT WHEN NOT FOUND;
                    v_suma_min := v_suma_min + v_stavka_red.vreme_pripreme_min;
                    v_suma_max := v_suma_max + v_stavka_red.vreme_pripreme_max;
                    v_brojac := v_brojac + 1;
                END LOOP;
                CLOSE cur_stavke_kategorije;
            
                IF v_brojac = 0 THEN
                    r_min_vreme := 15;
                    r_max_vreme := 25;
                ELSE
                    r_min_vreme := ROUND(v_suma_min::NUMERIC / v_brojac);
                    r_max_vreme := ROUND(v_suma_max::NUMERIC / v_brojac);
                END IF;
            EXCEPTION
                WHEN SQLSTATE 'P0001' THEN
                    RAISE EXCEPTION '%', SQLERRM;
                WHEN OTHERS THEN
                    RAISE EXCEPTION 'Greska prilikom izvrsavanja analize: %', SQLERRM;
            END;
            $$ LANGUAGE plpgsql;
            """;

        entityManager.createNativeQuery(sql).executeUpdate();
    }
}