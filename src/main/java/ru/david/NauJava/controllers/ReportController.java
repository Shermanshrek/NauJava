package ru.david.NauJava.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.david.NauJava.entity.Report;
import ru.david.NauJava.services.ReportService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
        logger.info("ReportController initialized");
    }

    @GetMapping
    public ResponseEntity<String> getReportsInfo() {
        logger.info("GET request to /api/reports");
        return ResponseEntity.ok("""
            <html>
                <body>
                    <h1>API отчетов</h1>
                    <p>Для создания отчета отправьте POST запрос на /api/reports</p>
                    <p>Для получения отчета отправьте GET запрос на /api/reports/{id}</p>
                    <p>Пример: GET /api/reports/1</p>
                </body>
            </html>
            """);
    }

    @PostMapping
    public ResponseEntity<?> createReport() {
        try {
            logger.info("POST request to create report");
            Long reportId = reportService.createReport();
            logger.info("Report created with ID: {}", reportId);

            CompletableFuture<Void> future = reportService.generateReportAsync(reportId);
            logger.info("Async report generation started for ID: {}", reportId);

            return ResponseEntity.ok(reportId);
        } catch (Exception e) {
            logger.error("Error creating report: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Ошибка при создании отчета: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getReport(@PathVariable Long id) {
        try {
            logger.info("GET request for report with ID: {}", id);
            Report report = reportService.getReportById(id);

            if (report == null) {
                logger.warn("Report not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            logger.info("Report status: {}", report.getStatus());

            return switch (report.getStatus()) {
                case CREATED -> ResponseEntity.accepted().body("Отчет еще формируется...");
                case COMPLETED -> ResponseEntity.ok()
                        .header("Content-Type", "text/html; charset=UTF-8")
                        .body(report.getContent());
                case ERROR -> ResponseEntity.internalServerError()
                        .body("При формировании отчета произошла ошибка: " + report.getContent());
                default -> ResponseEntity.internalServerError().body("Неизвестный статус отчета");
            };
        } catch (Exception e) {
            logger.error("Error getting report with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}