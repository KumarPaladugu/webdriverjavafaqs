package users.simulating.HowToSimulateUserDelayTest.withEventFiringWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Random;

public class HowToSimulateUserDelayTest {

    private WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    public void simulateUserDelayWithEventFiringWebDriver(){


        driver = new UserDelayDriver(new FirefoxDriver(), 5, 10).getWrappedDriver();
        //driver = new FirefoxDriver();
        driver.get("https://testpages.herokuapp.com/styled/index.html");
        driver.findElement(By.id("basicpagetest")).click();
        driver.findElement(By.linkText("Index")).click();
        driver.findElement(By.id("htmlformtest")).click();

        driver.findElement(By.name("username")).sendKeys("Bob");
        driver.findElement(By.name("comments")).sendKeys("These are bob's comments");

        // TODO: add id's to the buttons
        //driver.findElement(By.id("HTMLFormElements")).submit();
        driver.findElements(By.name("submitbutton")).get(1).click();

        Assertions.assertEquals("Bob",
                new WebDriverWait(driver,10).
                        until(ExpectedConditions.elementToBeClickable(
                                By.id("_valueusername"))).getText());

        driver.close();

    }

    private class UserDelayDriver implements WrapsDriver {
        EventFiringWebDriver driver;

        public UserDelayDriver(final WebDriver aDriver, final int shortestWait, final int maximumWait) {
            driver = new EventFiringWebDriver(aDriver);
            driver.register(new UserDelaysEvents(shortestWait, maximumWait));
        }

        @Override
        public WebDriver getWrappedDriver() {
            return driver;
        }

        private class UserDelaysEvents extends AbstractWebDriverEventListener {
            private final int shortestWait;
            private final int longestWait;

            public UserDelaysEvents(final int shortestWait, final int maximumWait) {
                this.shortestWait = shortestWait;
                this.longestWait = maximumWait;
            }

            @Override
            public void beforeClickOn(final WebElement element, final WebDriver driver) {
                userWaitsForSomeTime();
                super.beforeClickOn(element, driver);
            }

            @Override
            public void beforeChangeValueOf(final WebElement element, final WebDriver driver, final CharSequence[] keysToSend) {
                userWaitsForSomeTime();
                super.beforeChangeValueOf(element, driver, keysToSend);
            }

            private void userWaitsForSomeTime() {
                final Random rnd = new Random();
                final int seconds = rnd.nextInt(longestWait - shortestWait);
                System.out.println(String.format("Wait for %d seconds ", seconds + shortestWait));
                try {
                    Thread.sleep((seconds + shortestWait) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
