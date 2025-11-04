package ru.david.NauJava.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.david.NauJava.services.UserService;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserService userService;

    public AdminInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        //создаем админа, если его нет
        if(userService.findByUsername("admin").isEmpty()) {
            userService.createAdminUser(
                    "admin",
                    "admin123",
                    "System",
                    "Administrator"
            );
        }
    }
}
