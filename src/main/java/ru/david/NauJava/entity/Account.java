package ru.david.NauJava.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(precision = 15, scale = 2)
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    private String currency = "RUB";

    private Boolean isArchived = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "accounts", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "fromAccounts", cascade = CascadeType.ALL)
    private List<Transfer> outgoingTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "toAccounts", cascade = CascadeType.ALL)
    private List<Transfer> incomingTransfers = new ArrayList<>();

    public Account() {
    }

    public Account(String name, String type, BigDecimal initialBalance, BigDecimal currentBalance, LocalDateTime createdAt) {
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance;
        this.currentBalance = currentBalance;
        this.createdAt = createdAt;
    }

    public void updateBalance(BigDecimal amount, boolean isIncome) {
        if (isIncome) {
            this.currentBalance = currentBalance.add(amount);
        } else {
            this.currentBalance = currentBalance.subtract(amount);
        }
    }
}
