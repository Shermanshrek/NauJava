package ru.david.NauJava.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.david.NauJava.entity.Report;
import ru.david.NauJava.entity.ReportStatus;
import ru.david.NauJava.entity.Account;
import ru.david.NauJava.repository.ReportRepository;
import ru.david.NauJava.repository.UserRepository;
import ru.david.NauJava.repository.AccountRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TemplateEngine templateEngine;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         AccountRepository accountRepository,
                         TemplateEngine templateEngine) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.templateEngine = templateEngine;
        logger.info("ReportService initialized successfully with TemplateEngine");
    }

    public Report getReportById(Long id) {
        try {
            logger.debug("Getting report by ID: {}", id);
            return reportRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Error getting report by ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    private void generateReport(Long reportId) {
        Report report = null;
        try {
            logger.info("Starting report generation for ID: {}", reportId);

            report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

            long totalStartTime = System.currentTimeMillis();

            AtomicLong userCount = new AtomicLong(0);
            AtomicLong userTime = new AtomicLong(0);
            AtomicReference<List<Account>> accountsRef = new AtomicReference<>();
            AtomicLong accountsTime = new AtomicLong(0);

            Thread userCountThread = new Thread(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    long count = userRepository.count();
                    long elapsed = System.currentTimeMillis() - startTime;

                    userCount.set(count);
                    userTime.set(elapsed);

                    logger.debug("User count thread completed: {} users, {} ms", count, elapsed);
                } catch (Exception e) {
                    logger.error("Error in user count thread: {}", e.getMessage(), e);
                }
            });

            Thread accountsThread = new Thread(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    List<Account> accounts = (List<Account>) accountRepository.findAll();
                    long elapsed = System.currentTimeMillis() - startTime;

                    accountsRef.set(accounts);
                    accountsTime.set(elapsed);

                    logger.debug("Accounts thread completed: {} accounts, {} ms", accounts.size(), elapsed);
                } catch (Exception e) {
                    logger.error("Error in accounts thread: {}", e.getMessage(), e);
                }
            });

            logger.debug("Starting threads...");
            userCountThread.start();
            accountsThread.start();

            userCountThread.join();
            accountsThread.join();
            logger.debug("All threads completed");

            long finalUserCount = userCount.get();
            List<Account> finalAccounts = accountsRef.get();
            long finalUserTime = userTime.get();
            long finalAccountsTime = accountsTime.get();
            long totalTime = System.currentTimeMillis() - totalStartTime;

            logger.info("Report data collected - Users: {}, Accounts: {}, Total time: {}ms",
                    finalUserCount, finalAccounts != null ? finalAccounts.size() : 0, totalTime);

            String htmlContent = generateHtmlReportWithThymeleaf(finalUserCount, finalAccounts, finalUserTime, finalAccountsTime, totalTime);

            report.setContent(htmlContent);
            report.setStatus(ReportStatus.COMPLETED);
            reportRepository.save(report);

            logger.info("Report generation successfully completed for ID: {}", reportId);

        } catch (Exception e) {
            logger.error("Error generating report for ID {}: {}", reportId, e.getMessage(), e);
            if (report != null) {
                try {
                    report.setStatus(ReportStatus.ERROR);
                    report.setContent("Ошибка при формировании отчета: " + e.getMessage());
                    reportRepository.save(report);
                } catch (Exception saveEx) {
                    logger.error("Failed to save error status for report {}: {}", reportId, saveEx.getMessage());
                }
            }
        }
    }

    private String generateHtmlReportWithThymeleaf(long userCount, List<Account> accounts,
                                                   long userTime, long accountsTime, long totalTime) {
        try {
            Context context = new Context();

            context.setVariable("userCount", userCount);
            context.setVariable("accounts", accounts);
            context.setVariable("userTime", userTime);
            context.setVariable("accountsTime", accountsTime);
            context.setVariable("totalTime", totalTime);
            context.setVariable("generationDate", new Date());

            return templateEngine.process("report-template", context);

        } catch (Exception e) {
            logger.error("Error generating HTML report with Thymeleaf: {}", e.getMessage(), e);
            return "<html><body><h1>Ошибка при формировании отчета</h1><p>" + e.getMessage() + "</p></body></html>";
        }
    }

    public Long createReport() {
        try {
            logger.debug("Creating new report");
            Report report = new Report();
            Report savedReport = reportRepository.save(report);
            logger.info("Report created with ID: {}", savedReport.getId());
            return savedReport.getId();
        } catch (Exception e) {
            logger.error("Error creating report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create report", e);
        }
    }

    @Async
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        logger.info("Starting async report generation for ID: {}", reportId);

        return CompletableFuture.runAsync(() -> {
            try {
                generateReport(reportId);
                logger.info("Async report generation completed for ID: {}", reportId);
            } catch (Exception e) {
                logger.error("Critical error in async report generation for ID {}: {}", reportId, e.getMessage(), e);
            }
        });
    }
}