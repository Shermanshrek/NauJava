package ru.david.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.david.NauJava.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByType(String type);

    List<Category> findByTypeAndIsActiveTrue(String type);

    Optional<Category> findByName(String name);

    List<Category> findByNameContainingIgnoreCase(String name);

    List<Category> findByIsActive(Boolean isActive);

    List<Category> findAllByOrderByDisplayOrderAsc();
}
