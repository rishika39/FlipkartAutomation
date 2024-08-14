package demo.wrappers;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
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
        clickElement(By.xpath("//div[text()='" + criteria + "']"));
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
        clickElement(By.xpath("//*[@id=\"container\"]/div/div[3]/div[1]/div[1]/div/div/div/section[5]/div[2]/div/div[1]/div/label/div[1]"));
    }

    public long countItemsWithRatingLessThanOrEqual(int rating) {
        List<WebElement> starRatingElements = getElements(By.xpath("//div[@class='_3LWZlK']"));
        return starRatingElements.stream()
            .map(element -> Double.parseDouble(element.getText()))
            .filter(star -> star <= rating)
            .count();
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
