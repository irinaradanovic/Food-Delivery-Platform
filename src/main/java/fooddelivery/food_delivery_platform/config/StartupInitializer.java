package fooddelivery.food_delivery_platform.config;

import fooddelivery.food_delivery_platform.service.MeniService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StartupInitializer {

    private final MeniService meniService;

    public StartupInitializer(MeniService meniService) {
        this.meniService = meniService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // proveravamo odmah pri popunjavanju baze da li su sezonski meniji sinhronizovani
        meniService.syncSeasonalMenus();
        System.out.println("Sezonski meniji su sinhronizovani pri pokretanju.");
    }
}