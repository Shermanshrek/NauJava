package ru.david.NauJava.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.david.NauJava.entity.User;
import ru.david.NauJava.repository.UserRepository;

@Configuration
@Profile("test")
public class TestDataInitializer {

    @Bean
    public CommandLineRunner initTestData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", "admin123", "System", "Administrator");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.addRole("ADMIN");
                admin.addRole("USER");
                userRepository.save(admin);
            }

            // Можно добавить других тестовых пользователей
            if (userRepository.findByUsername("testuser").isEmpty()) {
                User testUser = new User("testuser", "testpass", "Test", "User");
                testUser.setPassword(passwordEncoder.encode("testpass"));
                testUser.addRole("USER");
                userRepository.save(testUser);
            }
        };
    }
}