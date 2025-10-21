package ru.david.NauJava.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppInfoConfig {
    @Value("${app.name}")
    private String appName;
    @Value("${app.version}")
    private String appVersion;

    @PostConstruct
    public void printAppInfo() {
        System.out.println("=== " + appName + " v" + appVersion + " ===");
    }
}
