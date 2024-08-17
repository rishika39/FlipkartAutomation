package demo.wrappers;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wrappers {
    private WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }
    public WebDriver getDriver() {
        return this.driver;
    }
    public void goToUrl(String url) {
        driver.get(url);
    }

    /*public void closeLoginPopup() {
        clickElement(By.xpath("//button[contains(text(), '✕')]"));
    }*/

    public void searchItem(String item) {
        enterText(By.name("q"), item + "\n");
    }

    public void sortItemsBy(String criteria) {
        clickElement(By.xpath("//div[text()='Popularity']"));
    }

    public void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    public void enterText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.sendKeys(text);
    }

    public WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    public void filterByRating(int stars) {
        clickElement(By.xpath("//*[@id='container']/div/div[3]/div[1]/div[1]/div/div/div/section[5]/div[2]/div/div[1]/div/label/div[1]"));
    }

    public long countItemsWithRatingLessThanOrEqual(int rating) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Adjust the timeout as needed
    int retryCount = 0;
    int maxRetries = 3; // Maximum number of retries

    while (retryCount < maxRetries) {
        try {
            List<WebElement> starRatingElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//li[contains(@class, 'J+igdf') and contains(text(), 'Star Rating')]")));

            return starRatingElements.stream()
                .map(element -> {
                    String text = element.getText().trim(); // Get and trim text from the <li> element
                    System.out.println("Extracted Text: " + text); // Debug: Print the extracted text
                    try {
                        String[] parts = text.split(" ");
                        if (parts.length > 0) {
                            return Integer.parseInt(parts[0]); // Assuming the first part is the number
                        } else {
                            return -1; // Return -1 if the split did not work as expected
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing rating from text: " + text);
                        return -1; // Return -1 for invalid or non-numeric ratings
                    }
                })
                .filter(star -> star <= rating && star > 0) // Filter based on the specified rating
                .count();
        } catch (StaleElementReferenceException e) {
            System.out.println("StaleElementReferenceException encountered. Retrying... (" + (retryCount + 1) + "/" + maxRetries + ")");
            retryCount++;
            // Wait briefly before retrying to give the DOM time to stabilize
            try {
                Thread.sleep(500); // Adjust the sleep time as necessary
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    // If the loop completes without success, return 0 or throw an exception
    return 0;
    }

    public int extractReviewCount(WebElement element) {
        String text = element.findElement(By.xpath(".//span[contains(text(), 'Ratings &')]")).getText();
        String[] parts = text.split(" ");
        try {
            return Integer.parseInt(parts[0].replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
