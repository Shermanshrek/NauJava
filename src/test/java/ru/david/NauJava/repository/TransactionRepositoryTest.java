package ru.david.NauJava.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.david.NauJava.entity.Account;
import ru.david.NauJava.entity.Category;
import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.repository.criteriaAPI.TransactionCustomRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
public class TransactionRepositoryTest {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final TransactionCustomRepositoryImpl transactionCustomRepository;

    @Test
    void testFindByDateBetweenAndAmountGreaterThanEqual_QueryMethod() {
        Category cat = createCategory("Еда", "expense");
        Account acc = createAccount("Карта", "debit_card");
        Transaction tx1 =  createTransaction(cat, acc, BigDecimal.valueOf(1500),
                LocalDateTime.of(2025, 1, 15, 0, 0), "Продукты");
        Transaction tx2 = createTransaction(cat, acc, BigDecimal.valueOf(500),
                LocalDateTime.of(2025, 1, 20, 23, 59), "Кафе");

        List<Transaction> result = transactionRepository.findByDateBetweenAndAmountGreaterThanEqual(
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 31, 23, 59),
                BigDecimal.valueOf(1000)
        );
        assertEquals(1, result.size());
        assertEquals(1500, result.getFirst().getAmount().intValue());
    }

    @Test
    void testFindByCategoryName_JPQL() {
        Category cat = createCategory("Транспорт", "expense");
        Account acc = createAccount("Наличные", "cash");
        Transaction tx1 = createTransaction(cat, acc, BigDecimal.valueOf(500),
                LocalDateTime.now(), "Такси");
        List<Transaction> result = transactionRepository.findByCategoryName("Транспорт");

        assertEquals(1, result.size());
        assertEquals("Транспорт", result.getFirst().getCategory().getName());
    }

    @Test
    void testFindByCategoryType_JPQL() {
        Category incomeCategory = createCategory("Зарплата", "income");
        Category expenseCategory = createCategory("Еда", "expense");
        Account acc = createAccount("Счет", "debit_card");

        Transaction tx1 = createTransaction(incomeCategory, acc, BigDecimal.valueOf(50000),
                LocalDateTime.now(), "Зарплата");
        Transaction tx2 = createTransaction(expenseCategory, acc, BigDecimal.valueOf(1500),
                LocalDateTime.now(), "Продукты");

        List<Transaction> incomes = transactionRepository.findByCategoryName("income");
        List<Transaction> expenses = transactionRepository.findByCategoryName("expense");

        assertEquals(1, incomes.size());
        assertEquals(1, expenses.size());
    }

    @Test
    void testFindByAccountName_JPQL() {
        Category category = createCategory("Еда", "expense");
        Account acc = createAccount("Кредитка", "credit_card");

        Transaction tx1 = createTransaction(category, acc, BigDecimal.valueOf(1000),
                LocalDateTime.now(), "Ресторан");

        List<Transaction> result = transactionRepository.findByAccountName("Кредитка");
        assertEquals(1, result.size());
        assertEquals("Кредитка", result.getFirst().getAccounts().getName());
    }

    @Test
    void testGetTotalAmountByCategoryAndDataRange_JPQL() {
        Category category = createCategory("Еда", "expense");
        Account acc = createAccount("Карта", "debit_card");

        Transaction tx1 = createTransaction(category, acc, BigDecimal.valueOf(1000),
                LocalDateTime.of(2025, 1, 15, 15, 0), "Продукты");
        Transaction tx2 = createTransaction(category, acc, BigDecimal.valueOf(500),
                LocalDateTime.of(2025, 1, 20, 21, 52), "Кафе");

        BigDecimal total = transactionRepository.getTotalAmountByCategoryAndDateRange(
                category.getId(),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );

        assertNotNull(total);
        assertEquals(1500, total.intValue());
    }

    @Test
    void testFindByCategoryName_CriteriaAPI() {
        Category category = createCategory("Развлечения", "expense");
        Account acc = createAccount("Карта", "debit_card");

        Transaction tx1 = createTransaction(category, acc, BigDecimal.valueOf(2000),
                LocalDateTime.now(), "Кино");
        List<Transaction> result = transactionCustomRepository.findByCategoryNameCustom("Развлечения");

        assertEquals(1, result.size());
        assertEquals("Развлечения", result.getFirst().getCategory().getName());
    }

    @Test
    void testFindByDateBetweenAndAmountGreaterThanEqual_CriteriaAPI() {
        Category category = createCategory("Еда", "expense");
        Account account = createAccount("Карта", "debit_card");

        createTransaction(category, account, BigDecimal.valueOf(2000), LocalDateTime.of(2025, 1, 10, 20, 0), "Ресторан");
        createTransaction(category, account, BigDecimal.valueOf(800), LocalDateTime.of(2025, 1, 25, 19, 34), "Кафе");

        List<Transaction> result = transactionCustomRepository.findByDateBetweenAndAmountGreaterThanEqualCustom(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                BigDecimal.valueOf(1500)
        );

        assertEquals(1, result.size());
        assertEquals(2000, result.getFirst().getAmount().intValue());
    }

    // Вспомогательные методы для создания тестовых данных
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
