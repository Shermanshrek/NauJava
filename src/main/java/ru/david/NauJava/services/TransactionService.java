package ru.david.NauJava.services;

import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.entity.TransactionType;

import java.util.List;

public interface TransactionService {
    void createTransaction(double amount, String category,
                           TransactionType type, String description);
    void deleteTransaction(Long id);

    Transaction findById(Long id);

    void updateTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();
}
