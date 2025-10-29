package ru.david.NauJava.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info();
        info.setTitle("Personal Finance Tracker API");
        info.setVersion("1.0");
        info.setDescription("API для системы учета личных финансов");

        return new OpenAPI().info(info);
    }
}