package ru.david.NauJava.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.david.NauJava.entity.Category;
import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.repository.CategoryRepository;
import ru.david.NauJava.repository.TransactionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repo;
    private final TransactionRepository transactRepo;

    @Transactional
    public void deleteCategory(Long id) {
        Category categoryToDelete = repo.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Category not found")
                );
        Category defaultCategory = getOrCreateDefaultCategory(categoryToDelete.getType());
        List<Transaction> transactionsToUpdate = transactRepo.findByCategoryName(categoryToDelete.getName());

        if (!transactionsToUpdate.isEmpty()) {
            for(Transaction transaction : transactionsToUpdate) {
                transaction.setCategory(defaultCategory);
            }
        }
        transactRepo.saveAll(transactionsToUpdate);
        repo.delete(categoryToDelete);
    }

    @Transactional
    protected Category getOrCreateDefaultCategory(String type) {
        String defaultCategoryName = "Другое";

        return repo.findByName(defaultCategoryName)
                .orElseGet(
                        () -> {
                            Category newDefaultCategory = new Category(
                                    defaultCategoryName,
                                    type,
                                    "Категория по умолчанию для переноса транзакций"
                            );
                            newDefaultCategory.setColor("#95a5a6");
                            return repo.save(newDefaultCategory);
                        });
    }
}
