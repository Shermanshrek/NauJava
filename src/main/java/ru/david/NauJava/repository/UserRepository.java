package ru.david.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.david.NauJava.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
