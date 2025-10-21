package ru.david.NauJava.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.entity.TransactionType;
import ru.david.NauJava.repository.TransactionRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepositoryImpl repository;

    public TransactionServiceImpl(TransactionRepositoryImpl repository) {
        this.repository = repository;
    }

    @Override
    public void createTransaction(double amount, String category, TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setType(type);
        transaction.setDescription(description);
        repository.create(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        repository.delete(id);
    }

    @Override
    public Transaction findById(Long id) {
        return repository.read(id);
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        repository.update(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }
}
