package ru.david.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.david.NauJava.entity.Budget;

import java.util.List;

@Repository
public interface BudgetRepository extends CrudRepository<Budget, Integer> {
    List<Budget> findByIsActiveTrue();
    List<Budget> findByPeriod(String period);
}
