package ru.david.NauJava.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReportControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void getReportsInfo_ReturnsHtmlPage() {
        given()
                .when()
                .get("/api/reports")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("API отчетов"));
    }

    @Test
    void createReport_ReturnsReportId() {
        given()
                .when()
                .post("/api/reports")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    void getReport_NonExistentId_ReturnsNotFound() {
        given()
                .when()
                .get("/api/reports/9999")
                .then()
                .statusCode(404);
    }

    @Test
    void getReport_InvalidId_ReturnsBadRequest() {
        given()
                .when()
                .get("/api/reports/invalid")
                .then()
                .statusCode(404);
    }
}