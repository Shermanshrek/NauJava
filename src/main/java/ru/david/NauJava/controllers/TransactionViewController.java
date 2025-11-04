package ru.david.NauJava.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.david.NauJava.entity.Transaction;
import ru.david.NauJava.repository.TransactionRepository;

@Controller
@RequestMapping("/transactions")
public class TransactionViewController {
    private final TransactionRepository transactionRepository;

    public TransactionViewController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/list")
    public String listTransactions(Model model) {
        Iterable<Transaction> transactions = transactionRepository.findAll();
        model.addAttribute("transactions", transactions);
        return "transactionList";
    }
}
