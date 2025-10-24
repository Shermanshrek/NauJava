package ru.david.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.david.NauJava.entity.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    List<Account> findByType(String type);

    List<Account> findByIsArchivedFalse();

    Optional<Account> findByName(String name);

    List<Account> findByNameContainingIgnoreCase(String name);

    List<Account> findByCurrency(String currency);

    List<Account> findAllByOrderByNameAsc();
}
