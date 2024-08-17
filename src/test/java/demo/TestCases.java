package demo;

import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;

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
        wrapper.searchItem("iPhone");
    
        // Locate all product elements
        List<WebElement> items = wrapper.getElements(By.xpath("//div[@class='yKfJKb row']"));
        
        for (WebElement item : items) {
            try {
                // Locate the title and discount within the product element
                String title = item.findElement(By.xpath(".//div[@class='KzDlHZ']")).getText().trim();
                String discount = item.findElement(By.xpath(".//div[@class='UkUFwK']/span")).getText().trim();
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
    
        // Select 4 stars and above
        wrapper.clickElement(By.xpath("(//div[@class='XqNaEv'])[1]"));
    
        // Wait for the page to load the results
        WebDriverWait wait = new WebDriverWait(wrapper.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='_75nlfW']")));
    
        // Get all items with 4 stars and above
        List<WebElement> items = wrapper.getElements(By.xpath("//div[@class='_75nlfW']"));
    
        // List to store items with reviews and ratings
        List<Item> itemList = new ArrayList<>();
    
        for (WebElement item : items) {
            try {
                // Get the title
                String title = item.findElement(By.xpath(".//a[@class='wjcEIp']")).getText().trim();
                
                // Get the image URL
                String imageUrl = item.findElement(By.xpath(".//a[@class='VJA3rP']//img")).getAttribute("src").trim();
                
                // Get the number of reviews
                String reviewsText = item.findElement(By.xpath(".//span[@class='Wphh3N']")).getText().trim();
                int reviewsCount = Integer.parseInt(reviewsText.replaceAll("[^0-9]", ""));
                
                // Store the item details
                itemList.add(new Item(title, imageUrl, reviewsCount));
            } catch (Exception e) {
                System.out.println("Error processing item: " + e.getMessage());
            }
        }
    
        // Sort items by the number of reviews in descending order and get the top 5
        List<Item> topItems = itemList.stream()
                                      .sorted((i1, i2) -> Integer.compare(i2.getReviewsCount(), i1.getReviewsCount()))
                                      .limit(5)
                                      .collect(Collectors.toList());
    
        // Print the details of the top 5 items
        for (Item item : topItems) {
            System.out.println("Title: " + item.getTitle());
            System.out.println("Image URL: " + item.getImageUrl());
            System.out.println("Number of Reviews: " + item.getReviewsCount());
            System.out.println();
        }
    }
    
    // Define an inner class to store item details
    private static class Item {
        private String title;
        private String imageUrl;
        private int reviewsCount;
    
        public Item(String title, String imageUrl, int reviewsCount) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.reviewsCount = reviewsCount;
        }
    
        public String getTitle() {
            return title;
        }
    
        public String getImageUrl() {
            return imageUrl;
        }
    
        public int getReviewsCount() {
            return reviewsCount;
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
