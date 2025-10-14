package ru.david.NauJava.console;

import org.springframework.stereotype.Component;
import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.entity.TransactionType;
import ru.david.NauJava.services.TransactionServiceImpl;

import java.util.List;

@Component
public class CommandProcessor {
    private final TransactionServiceImpl transactionService;

    public CommandProcessor(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    public void processCommand(String input){
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0) return;

        try{
            switch (parts[0].toLowerCase()) {
                case "income" -> handleIncome(parts);
                case "expense" -> handleExpense(parts);
                case "delete" -> handleDelete(parts);
                case "list" -> handleList();
//                case "report" -> handleReport();
                case "exit" -> System.out.println("Выход из программы...");
                default -> System.out.println("Неизвестная команда. Доступны: income, expense, delete, list, exit");
            }
        } catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }


    private void handleDelete(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Использование: delete <id>");
            return;
        }
        Long id = Long.parseLong(parts[1]);
        transactionService.deleteTransaction(id);
        System.out.println("Транзакция удалена.");
    }

    private void handleIncome(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Использование: income <сумма> <категория> [описание]");
            return;
        }
        double amount = Double.parseDouble(parts[1]);
        String category = parts[2];
        String desc = parts.length > 3 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 3, parts.length)) : "";
        transactionService.createTransaction(amount, category, TransactionType.INCOME, desc);
        System.out.println("Доход добавлен.");
    }

    private void handleExpense(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Использование: expense <сумма> <категория> [описание]");
            return;
        }
        double amount = Double.parseDouble(parts[1]);
        String category = parts[2];
        String desc = parts.length > 3? String.join(" ", java.util.Arrays.copyOfRange(parts, 3, parts.length)) : "";
        transactionService.createTransaction(amount, category, TransactionType.EXPENSE, desc);
        System.out.println("Расход добавлен.");
    }

    private void handleList() {
        List<Transaction> all = transactionService.getAllTransactions();
        if (all.isEmpty()) {
            System.out.println("Нет транзакций.");
            return;
        }
        for (Transaction t : all) {
            System.out.printf("%d | %s | %s | %.2f | %s%n",
                    t.getId(), t.getType(), t.getCategory(), t.getAmount(), t.getDescription());
        }
    }
}
