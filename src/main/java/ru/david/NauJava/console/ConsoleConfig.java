package ru.david.NauJava.console;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class ConsoleConfig {
    private final CommandProcessor commandProcessor;

    public ConsoleConfig(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("Команды: income, expense, delete, list, exit");
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.println("> ");
                    String input = scanner.nextLine();
                    if ("exit".equals(input.trim())) {
                        System.out.println("Выход...");
                        break;
                    }
                    commandProcessor.processCommand(input);
                }
            }
        };
    }
}
