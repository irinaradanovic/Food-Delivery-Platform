package fooddelivery.food_delivery_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String putanjaDoSlika = Paths.get("src/main/resources/static/images/food/").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/images/food/**")
                .addResourceLocations(putanjaDoSlika)
                .setCachePeriod(0);
    }
}