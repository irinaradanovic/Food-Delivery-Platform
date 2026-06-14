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
                --ORDER BY meni_id DESC
                LIMIT 1;
            
                --ako se kreira potpuno novi meni, nema stare verzije, triger nepotreban
                IF v_stari_meni_id IS NULL THEN
                    RETURN NEW;
                END IF;
            
                /*INSERT INTO stavke_menija (meni_id, proizvod_id, vreme_pripreme_min, vreme_pripreme_max, cena, dostupno, obrisan)
                SELECT NEW.meni_id, proizvod_id, vreme_pripreme_min, vreme_pripreme_max, cena, dostupno, obrisan
                FROM stavke_menija
                WHERE meni_id = v_stari_meni_id 
                  AND obrisan = FALSE
                 
                ON CONFLICT (meni_id, proizvod_id) DO NOTHING;  */
            
                -- deaktivacija prethodne verzije
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
}