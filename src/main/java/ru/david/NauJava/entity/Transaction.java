package ru.david.NauJava.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    private LocalDateTime date;

    private String description;

    private String location;

    private Boolean isRecurring = false;

    private String recurrencePattern;

    private LocalDateTime createdAt;

    public Transaction() {
        this.date = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public Transaction(Category category, Account account, BigDecimal amount, LocalDateTime date) {
        this();
        this.category = category;
        this.account = account;
        this.amount = amount;
        this.date = date;
    }

    public boolean isIncome(){
        return "income".equals(category.getType());
    }

    public boolean isExpense(){
        return "expense".equals(category.getType());
    }
}
