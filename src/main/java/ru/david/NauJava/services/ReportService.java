package ru.david.NauJava.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.david.NauJava.entity.Report;
import ru.david.NauJava.entity.ReportStatus;
import ru.david.NauJava.entity.Account;
import ru.david.NauJava.repository.ReportRepository;
import ru.david.NauJava.repository.UserRepository;
import ru.david.NauJava.repository.AccountRepository;

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

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         AccountRepository accountRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        logger.info("ReportService initialized successfully");
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

    private void generateReport(Long reportId) {
        Report report = null;
        try {
            logger.info("Starting report generation for ID: {}", reportId);

            report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

            logger.debug("Found report with status: {}", report.getStatus());

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

            String htmlContent = generateHtmlReport(finalUserCount, finalAccounts, finalUserTime, finalAccountsTime, totalTime);

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

    private String generateHtmlReport(long userCount, List<Account> accounts,
                                      long userTime, long accountsTime, long totalTime) {
        try {
            StringBuilder html = new StringBuilder();

            html.append("<!DOCTYPE html>")
                    .append("<html lang=\"ru\">")
                    .append("<head>")
                    .append("  <meta charset=\"UTF-8\">")
                    .append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                    .append("  <title>Статистика приложения - Отчет</title>")
                    .append("  <style>")
                    .append("    * { margin: 0; padding: 0; box-sizing: border-box; }")
                    .append("    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f5f5f5; padding: 20px; }")
                    .append("    .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }")
                    .append("    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }")
                    .append("    .header h1 { font-size: 2.5rem; margin-bottom: 10px; }")
                    .append("    .header p { font-size: 1.1rem; opacity: 0.9; }")
                    .append("    .content { padding: 30px; }")
                    .append("    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }")
                    .append("    .stat-card { background: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; border-radius: 5px; }")
                    .append("    .stat-card h3 { color: #495057; margin-bottom: 10px; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; }")
                    .append("    .stat-value { font-size: 2rem; font-weight: bold; color: #667eea; }")
                    .append("    .stat-unit { font-size: 0.9rem; color: #6c757d; margin-left: 5px; }")
                    .append("    .section { margin-bottom: 30px; }")
                    .append("    .section h2 { color: #495057; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 2px solid #e9ecef; }")
                    .append("    table { width: 100%; border-collapse: collapse; background: white; border-radius: 5px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }")
                    .append("    th { background: #667eea; color: white; padding: 12px 15px; text-align: left; font-weight: 600; }")
                    .append("    td { padding: 12px 15px; border-bottom: 1px solid #e9ecef; }")
                    .append("    tr:nth-child(even) { background: #f8f9fa; }")
                    .append("    tr:hover { background: #e3f2fd; }")
                    .append("    .no-data { text-align: center; color: #6c757d; padding: 20px; font-style: italic; }")
                    .append("    .timestamp { text-align: center; color: #6c757d; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e9ecef; }")
                    .append("  </style>")
                    .append("</head>")
                    .append("<body>")
                    .append("  <div class=\"container\">")
                    .append("    <div class=\"header\">")
                    .append("      <h1>📊 Статистика приложения</h1>")
                    .append("      <p>Автоматически сгенерированный отчет</p>")
                    .append("    </div>")
                    .append("    <div class=\"content\">")
                    .append("      <div class=\"stats-grid\">")
                    .append("        <div class=\"stat-card\">")
                    .append("          <h3>Пользователи</h3>")
                    .append("          <div class=\"stat-value\">").append(userCount).append("</div>")
                    .append("        </div>")
                    .append("        <div class=\"stat-card\">")
                    .append("          <h3>Время подсчета пользователей</h3>")
                    .append("          <div class=\"stat-value\">").append(userTime).append("<span class=\"stat-unit\">мс</span></div>")
                    .append("        </div>")
                    .append("        <div class=\"stat-card\">")
                    .append("          <h3>Время загрузки счетов</h3>")
                    .append("          <div class=\"stat-value\">").append(accountsTime).append("<span class=\"stat-unit\">мс</span></div>")
                    .append("        </div>")
                    .append("        <div class=\"stat-card\">")
                    .append("          <h3>Общее время</h3>")
                    .append("          <div class=\"stat-value\">").append(totalTime).append("<span class=\"stat-unit\">мс</span></div>")
                    .append("        </div>")
                    .append("      </div>")
                    .append("      <div class=\"section\">")
                    .append("        <h2>📋 Список счетов</h2>");

            if (accounts == null || accounts.isEmpty()) {
                html.append("        <div class=\"no-data\">Нет данных о счетах</div>");
            } else {
                html.append("        <table>")
                        .append("          <thead>")
                        .append("            <tr>")
                        .append("              <th>ID</th>")
                        .append("              <th>Название</th>")
                        .append("              <th>Тип</th>")
                        .append("              <th>Баланс</th>")
                        .append("              <th>Валюта</th>")
                        .append("            </tr>")
                        .append("          </thead>")
                        .append("          <tbody>");

                for (Account account : accounts) {
                    html.append("            <tr>")
                            .append("              <td>").append(account.getId()).append("</td>")
                            .append("              <td>").append(escapeHtml(account.getName())).append("</td>")
                            .append("              <td>").append(escapeHtml(account.getType())).append("</td>")
                            .append("              <td>").append(account.getCurrentBalance() != null ? account.getCurrentBalance() : "0.00").append("</td>")
                            .append("              <td>").append(escapeHtml(account.getCurrency())).append("</td>")
                            .append("            </tr>");
                }

                html.append("          </tbody>")
                        .append("        </table>");
            }

            html.append("      </div>")
                    .append("      <div class=\"timestamp\">")
                    .append("        Отчет сгенерирован: ").append(new java.util.Date()).append("</div>")
                    .append("    </div>")
                    .append("  </div>")
                    .append("</body>")
                    .append("</html>");

            return html.toString();
        } catch (Exception e) {
            logger.error("Error generating HTML report: {}", e.getMessage(), e);
            return "<html><body><h1>Ошибка при формировании отчета</h1><p>" + e.getMessage() + "</p></body></html>";
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}