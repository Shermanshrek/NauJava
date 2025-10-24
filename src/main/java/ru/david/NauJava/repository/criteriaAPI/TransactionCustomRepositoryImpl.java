package ru.david.NauJava.repository.criteriaAPI;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import ru.david.NauJava.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionCustomRepositoryImpl implements TransactionCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Transaction> findByCategoryNameCustom(String categoryName){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);

        Root<Transaction> root = query.from(Transaction.class);
        Join<Object, Object> categoryJoin = root.join("category");

        Predicate categoryNamePredicate = cb.equal(categoryJoin.get("name"), categoryName);
        query.where(categoryNamePredicate);
        query.orderBy(cb.desc(root.get("date")));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<Transaction> findByDateBetweenAndAmountGreaterThanEqualCustom(LocalDate startDate, LocalDate endDate, BigDecimal minAmount) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> root = query.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.between(root.get("date"), startDate, endDate));
        predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        query.orderBy(
                cb.desc(root.get("amount")),
                cb.desc(root.get("date"))
        );
        return em.createQuery(query).getResultList();
    }
}
