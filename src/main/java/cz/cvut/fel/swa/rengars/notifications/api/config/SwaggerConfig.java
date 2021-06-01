package cz.cvut.fel.swa.rengars.notifications.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            SwaggerResource wsResource = new SwaggerResource();
            wsResource.setName("Documentation");
            wsResource.setSwaggerVersion("3.0");
            wsResource.setLocation("/notifications.yaml");

            return List.of(wsResource);
        };
    }
}
