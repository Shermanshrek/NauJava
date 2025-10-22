package ru.david.NauJava.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@Getter
@Setter
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    private String period;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer alertThreshold = 90;

    private Boolean isActive = true;

    private LocalDateTime createdAt;

    public Budget() {
        this.createdAt = LocalDateTime.now();
    }

    public Budget(Category category, BigDecimal amount, String period, LocalDate startDate, LocalDate endDate) {
        this();
        this.category = category;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // метод для проверки активности бюджета
    public boolean isCurrentlyActive(){
        LocalDate today = LocalDate.now();
        return isActive &&
                !today.isBefore(startDate) &&
                !today.isAfter(endDate);
    }
}
