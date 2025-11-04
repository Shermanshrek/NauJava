package ru.david.NauJava.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.david.NauJava.entity.Account;
import ru.david.NauJava.entity.Category;
import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.repository.AccountRepository;
import ru.david.NauJava.repository.CategoryRepository;
import ru.david.NauJava.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CategoryServiceTest {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public CategoryServiceTest(CategoryService categoryService, CategoryRepository categoryRepository, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Test
    void testDeleteCategory_PositiveCase(){
        Category category = createCategory("Еда", "expense");
        Account acc = createAccount("Карта", "debit_card");

        Transaction tx1 = createTransaction(category, acc, BigDecimal.valueOf(1000),
                LocalDateTime.now(), "Продукты");
        Transaction tx2 = createTransaction(category, acc, BigDecimal.valueOf(500),
                LocalDateTime.now(), "Кафе");

        assertEquals(2, transactionRepository.findByCategoryName("Еда").size());
        assertTrue(categoryRepository.findByName("Еда").isPresent());

        categoryService.deleteCategory(category.getId());

        assertFalse(categoryRepository.findByName("Еда").isPresent());
        assertEquals(2, transactionRepository.findByCategoryName("Другое").size());
    }

    @Test
    void testDeleteCategory_NoTransactions(){
        Category category = createCategory("Пустая", "expense");

        categoryService.deleteCategory(category.getId());
        assertFalse(categoryRepository.findByName("Пустая").isPresent());
    }

    private Category createCategory(String name, String type) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setDescription("Описание " + name);
        return categoryRepository.save(category);
    }

    private Account createAccount(String name, String type) {
        Account account = new Account();
        account.setName(name);
        account.setType(type);
        account.setInitialBalance(BigDecimal.ZERO);
        account.setCurrentBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    private Transaction createTransaction(Category category, Account account, BigDecimal amount, LocalDateTime date, String description) {
        Transaction transaction = new Transaction();
        transaction.setCategory(category);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }
}
