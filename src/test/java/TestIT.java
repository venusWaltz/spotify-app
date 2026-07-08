import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class TestIT {
    static WebDriver driver;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        driver = new ChromeDriver();
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void login() throws InterruptedException {
        driver.get("http://127.0.0.1:8080");

        Duration duration = Duration.ofSeconds(10);
        WebDriverWait wait = new WebDriverWait(driver, duration);
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sign-in")));
        button.click();

        String result = driver.getCurrentUrl();
        String expected = "https://accounts.spotify.com/";
        Assertions.assertTrue(result.startsWith(expected));
    }

}
