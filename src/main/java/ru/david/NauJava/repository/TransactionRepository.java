package ru.david.NauJava.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.david.NauJava.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "transactions")
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    List<Transaction> findByDateBetweenAndAmountGreaterThanEqual(
            LocalDateTime date, LocalDateTime date2, BigDecimal amount);

    List<Transaction> findByDate(LocalDateTime date);

    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByAmountGreaterThanEqual(BigDecimal amount);

    List<Transaction> findByAmountLessThanEqual(BigDecimal amount);

    List<Transaction> findByDescriptionContainingIgnoreCase(String description);

    List<Transaction> findByIsRecurringTrue();

    @Query("select t from Transaction t where t.category.name = :categoryName")
    List<Transaction> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("select t from Transaction t where t.category.type = :categoryType")
    List<Transaction> findByCategoryType(@Param("categoryType") String categoryType);

    @Query("select t from Transaction t where t.accounts.name = :accountName")
    List<Transaction> findByAccountName(@Param("accountName") String accountName);

    @Query("select sum(t.amount) from Transaction t where t.category.id = :categoryId and " +
            "t.date between :startDate and :endDate")
    BigDecimal getTotalAmountByCategoryAndDateRange(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
