package ru.david.NauJava.repository.criteriaAPI;

import ru.david.NauJava.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionCustomRepository {
    List<Transaction> findByCategoryNameCustom(String categoryName);

    List<Transaction> findByDateBetweenAndAmountGreaterThanEqualCustom
            (LocalDate startDate, LocalDate endDate, BigDecimal minAmount);
}
