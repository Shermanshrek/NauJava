package ru.david.NauJava.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
@Getter
@Setter
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_accoun_id", nullable = false)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    private LocalDateTime date;

    private String description;

    @Column(name = "comission", precision = 15, scale = 2)
    private BigDecimal commission = BigDecimal.ZERO;

    @Column(name = "exchange_rate", precision = 10, scale = 4)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    private LocalDateTime createdAt;

    public Transfer(){
        this.createdAt = LocalDateTime.now();
        this.date = LocalDateTime.now();
    }

    public Transfer(Account fromAccount, Account toAccount, BigDecimal amount, LocalDateTime date) {
        this();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
    }

    public BigDecimal getTotalAmount() {
        return amount.add(commission);
    }
}
