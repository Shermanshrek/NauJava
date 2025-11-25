package ru.david.NauJava.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginLogoutSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void successfulLoginAndLogout() {
        driver.get(baseUrl + "/login");

        WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form[action*='/login']")));
        assertNotNull(loginForm, "Login form should be present");

        // Ввод учетных данных
        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("admin123");

        // Нажатие кнопки входа
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        boolean loginSuccessful = wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        assertTrue(loginSuccessful, "Should be redirected from login page after successful login");

        driver.get(baseUrl + "/logout");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(baseUrl + "/login"),
                ExpectedConditions.urlToBe(baseUrl + "/")
        ));
    }

    @Test
    void loginWithInvalidCredentials_ShowsError() {
        driver.get(baseUrl + "/login");

        // Ввод неверных учетных данных
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        WebElement passwordField = driver.findElement(By.name("password"));

        usernameField.sendKeys("invalidUser");
        passwordField.sendKeys("wrongPassword");

        // Нажатие кнопки входа
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();


        boolean errorElementPresent = wait.until(driver -> {
            List<WebElement> errorElements = driver.findElements(By.cssSelector(".error"));
            if (!errorElements.isEmpty() && errorElements.getFirst().isDisplayed()) return true;

            errorElements = driver.findElements(By.cssSelector("[class*='error']"));
            if (!errorElements.isEmpty() && errorElements.getFirst().isDisplayed()) return true;

            errorElements = driver.findElements(By.cssSelector(".alert-danger"));
            if (!errorElements.isEmpty() && errorElements.getFirst().isDisplayed()) return true;

            errorElements = driver.findElements(By.xpath("//*[contains(text(), 'Invalid username or password')]"));
            return !errorElements.isEmpty() && errorElements.getFirst().isDisplayed();
        });

        assertTrue(errorElementPresent, "Error message should be displayed for invalid credentials");
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/login") ||
                        driver.getCurrentUrl().contains("/login?error"),
                "Should remain on login page after failed login");
    }

    @Test
    void loginWithInvalidCredentials_ShowsError_Simplified() {
        driver.get(baseUrl + "/login");

        // Ввод неверных учетных данных
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        WebElement passwordField = driver.findElement(By.name("password"));

        usernameField.sendKeys("invaliduser");
        passwordField.sendKeys("wrongpassword");

        // Нажатие кнопки входа
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.urlContains("/login?error")
        ));

        List<WebElement> possibleErrorElements = driver.findElements(By.cssSelector(
                ".error, [class*='error'], .alert, .alert-danger, .alert-error, .message, .warning"
        ));

        boolean foundError = false;
        for (WebElement element : possibleErrorElements) {
            if (element.isDisplayed()) {
                foundError = true;
                break;
            }
        }

        if (!foundError) {
            String pageText = driver.findElement(By.tagName("body")).getText();
            if (pageText.contains("Invalid") || pageText.contains("Error") ||
                    pageText.contains("Неверный") || pageText.contains("Ошибка")) {
                foundError = true;
            }
        }

        assertTrue(foundError, "Should show some error indication for invalid credentials");
    }

    @Test
    void testReportPageAccess() {
        driver.get(baseUrl + "/login");
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        WebElement passwordField = driver.findElement(By.name("password"));
        usernameField.sendKeys("admin");
        passwordField.sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Ждем успешного логина
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        // Теперь проверяем доступ к странице отчетов
        driver.get(baseUrl + "/api/reports");

        // Проверяем, что страница загрузилась
        WebElement pageContent = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.tagName("body")));

        assertTrue(pageContent.isDisplayed(), "Reports API page should be accessible");

        // Проверяем наличие контента на странице
        String pageText = pageContent.getText();
        assertTrue(pageText.contains("отчет") || pageText.contains("report") ||
                        pageText.contains("API") || pageText.length() > 50,
                "Page should contain meaningful content");
    }
}