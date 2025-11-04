package ru.david.NauJava.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/search/by-date-amount")
    public List<Transaction> findByDateBetweenAndAmountGreaterThanEqual(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
            @RequestParam BigDecimal minAmount) {
        return transactionRepository.findByDateBetweenAndAmountGreaterThanEqual(
                startDate,
                endDate,
                minAmount
        );
    }

    @GetMapping("/search/by-category-name")
    public List<Transaction> findByCategoryName(@RequestParam String categoryName) {
        return transactionRepository.findByCategoryName(categoryName);
    }

    @GetMapping("/search/by-category-type")
    public List<Transaction> findByCategoryType(@RequestParam String categoryType) {
        return transactionRepository.findByCategoryType(categoryType);
    }

    @GetMapping("search/by-account-name")
    public List<Transaction> findByAccountName(@RequestParam String accountName) {
        return transactionRepository.findByAccountName(accountName);
    }

    @GetMapping("/total-by-category")
    public BigDecimal getTotalByCategoryAndDateRange(
            @RequestParam Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return transactionRepository.getTotalAmountByCategoryAndDateRange(
          categoryId,
          startDate,
          endDate
        );
    }
}
