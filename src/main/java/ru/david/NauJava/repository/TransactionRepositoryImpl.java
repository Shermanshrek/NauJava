package ru.david.NauJava.repository;

import org.springframework.stereotype.Component;
import ru.david.NauJava.entity.Transaction;

import java.util.List;

@Component
public class TransactionRepositoryImpl implements CrudRepository<Transaction, Long> {

    private Long nextId = 1L;
    private final List<Transaction> transactions;

    public TransactionRepositoryImpl(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void create(Transaction transaction) {
        transaction.setId(nextId++);
        transactions.add(transaction);
    }

    @Override
    public Transaction read(Long id) {
        return transactions
                .stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Transaction updated) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(updated.getId())) {
                transactions.set(i, updated);
                break;
            }
        }
    }

    @Override
    public void delete(Long id) {
        transactions.removeIf(t -> t.getId().equals(id));
    }

    @Override
    public List<Transaction> findAll() {
        return List.copyOf(transactions);
    }
}
