package demo;

import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import demo.wrappers.Wrappers;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TestCases {
    private ChromeDriver driver;
    private Wrappers wrapper;

    private static final Logger logger = Logger.getLogger(TestCases.class.getName());

    @Test
    public void testCase01() {
        wrapper.goToUrl("https://www.flipkart.com/");
        //wrapper.closeLoginPopup();
        wrapper.searchItem("Washing Machine");
        wrapper.sortItemsBy("Popularity");

        long count = wrapper.countItemsWithRatingLessThanOrEqual(4);
        System.out.println("Number of items with 4 stars or less: " + count);
    }

    @Test
    public void testCase02() {
        wrapper.goToUrl("https://www.flipkart.com/");
        //wrapper.closeLoginPopup();
        wrapper.searchItem("iPhone");

        List<WebElement> items = wrapper.getElements(By.xpath("//div[contains(@class,'_1AtVbE')]"));
        for (WebElement item : items) {
            try {
                String title = item.findElement(By.xpath(".//a[@class='IRpwTa']")).getText();
                String discount = item.findElement(By.xpath(".//div[contains(@class, '_3Ay6Sb')]/span")).getText();
                int discountPercentage = Integer.parseInt(discount.replaceAll("[^0-9]", ""));
                if (discountPercentage > 17) {
                    System.out.println("Title: " + title);
                    System.out.println("Discount: " + discountPercentage + "%");
                }
            } catch (Exception e) {
                System.out.println("Error processing item: " + e.getMessage());
            }
        }
    }

    @Test
    public void testCase03() {
        wrapper.goToUrl("https://www.flipkart.com/");
        //wrapper.closeLoginPopup();
        wrapper.searchItem("Coffee Mug");
        wrapper.filterByRating(4);

        List<WebElement> items = wrapper.getElements(By.xpath("//div[contains(@class,'_1AtVbE')]"));
        List<WebElement> top5Items = items.stream()
            .sorted((e1, e2) -> Integer.compare(wrapper.extractReviewCount(e2), wrapper.extractReviewCount(e1)))
            .limit(5)
            .collect(Collectors.toList());

        for (WebElement item : top5Items) {
            String title = item.findElement(By.xpath(".//a[@class='IRpwTa']")).getText();
            String imageURL = item.findElement(By.xpath(".//img")).getAttribute("src");
            System.out.println("Title: " + title);
            System.out.println("Image URL: " + imageURL);
        }
    }

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wrapper = new Wrappers(driver, Duration.ofSeconds(30));
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();
    }
}
