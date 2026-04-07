package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class WatchlistEbayTest extends BaseTest {

    // Declaring stable URL and user login information
    private static final String LOGIN_URL = "https://signin.ebay.com/signin";
    private static final String WATCHLIST_URL = "https://www.ebay.com/mye/myebay/watchlist";

    // Replace with valid eBay credentials
    private static final String VALID_EMAIL = "";
    private static final String VALID_PASSWORD = "";

    // Simulates human pause between actions
    private void humanDelay() throws InterruptedException {
        Random random = new Random();
        int delay = 500 + random.nextInt(1500); // random delay between 0.5s - 2s
        Thread.sleep(delay);
    }

    // Login once before all tests
    @BeforeClass
    public void loginToEbay() throws InterruptedException {
        super.setUp();

        driver.get(LOGIN_URL);
        humanDelay();

        WebElement enterEmail = driver.findElement(By.id("userid"));
        enterEmail.sendKeys(VALID_EMAIL);
        humanDelay();

        WebElement signInContinue = driver.findElement(By.id("signin-continue-btn"));
        signInContinue.click();
        humanDelay();

        WebElement passwordInput = driver.findElement(By.id("pass"));
        passwordInput.sendKeys(VALID_PASSWORD);

        WebElement submitLogin = driver.findElement(By.id("sgnBt"));
        submitLogin.click();
        humanDelay();
        Thread.sleep(2000);

        // Verify login successful
        WebElement detectMainPage = driver.findElement(By.xpath("//*[@id=\"gh\"]/nav/div[1]/span[1]/div/button/span"));
        String homePageGreeting = detectMainPage.getText().toLowerCase();
        Assert.assertTrue(homePageGreeting.contains("hi"), "User is not logged in");
    }

    // Verifies that an item can be added to watchlist
    @Test(priority = 1)
    public void testAddItemToWatchlist() throws InterruptedException {
        humanDelay();

        // Search for a product
        WebElement searchBox = driver.findElement(By.cssSelector("input[type='text'][placeholder*='Search']"));
        searchBox.sendKeys("laptop");

        WebElement searchEnter = driver.findElement(By.id("gh-search-btn"));
        searchEnter.click();

        // Get all clickable product image links
        List<WebElement> productLinks = driver.findElements(By.cssSelector("a.s-card__link.image-treatment"));

        // Skip the first one and take the second
        WebElement secondProductLink = productLinks.get(2);
        String productUrl = secondProductLink.getAttribute("href");
        driver.get(productUrl);
        humanDelay();

        // Click "Add to watchlist" button
        WebElement addToWatchlist = driver.findElement(By.id("watchBtn_btn_1"));
        addToWatchlist.click();
        humanDelay();
    }

    // Verifies that watchlist page displays the added item
    @Test(priority = 2)
    public void testWatchlistDisplaysAddedItem() throws InterruptedException {
        driver.get(WATCHLIST_URL);
        humanDelay();

        // Verify watchlist has items
        List<WebElement> watchlistItems = driver.findElements(By.cssSelector(".m-item-3"));
        Assert.assertFalse(watchlistItems.isEmpty(), "Watchlist is empty after adding an item");

        System.out.println("Watchlist displays added item(s)");
    }

    // Verifies that watchlist item count is displayed correctly
    @Test(priority = 3)
    public void testWatchlistItemCount() throws InterruptedException {
        driver.get(WATCHLIST_URL);
        humanDelay();

        // Get item count from watchlist
        List<WebElement> watchlistItems = driver.findElements(By.cssSelector(".m-item-3"));
        int actualCount = watchlistItems.size();

        System.out.println("Watchlist item count: " + actualCount);
        Assert.assertTrue(actualCount > 0, "Watchlist should have at least one item");
    }

    @Test(priority = 4)
    public void testViewItemFromWatchlist() throws InterruptedException {
        // Go to the watchlist page
        driver.get(WATCHLIST_URL);
        humanDelay();

        // Get all watchlist items
        List<WebElement> items = driver.findElements(By.cssSelector(".m-item-3 .m-item-3-col__column--image"));

        // Get the first item's link and click it
        WebElement firstItemLink = items.get(0).findElement(By.cssSelector("a"));
        firstItemLink.click();
        humanDelay();

        // Switch to the new tab/window if it opened in a new one
        String originalWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Verify the product page loaded
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("ebay.com/itm"), "Product page did not load");

        System.out.println(" Successfully viewed item from watchlist");
    }

    // Verifies that an item can be removed from watchlist
    @Test(priority = 5)
    public void testRemoveItemFromWatchlist() throws InterruptedException {
        driver.get(WATCHLIST_URL);
        humanDelay();

        List<WebElement> items = driver.findElements(By.cssSelector(".m-item-3"));
        int initialCount = items.size();

        if (initialCount > 0) {
            // Click the checkbox of the first item
            WebElement checkbox = items.get(0).findElement(By.cssSelector(".m-item-3-col__column--checkbox input.checkbox__control"));
            if (!checkbox.isSelected()) {
                checkbox.click();
                humanDelay();
            }

            // Click the Delete button
            WebElement deleteBtn = driver.findElement(By.xpath("//button[text()='Delete']"));
            deleteBtn.click();
            humanDelay();

            // Verify item removed
            List<WebElement> updatedItems = driver.findElements(By.cssSelector(".m-item-3"));
            int updatedCount = updatedItems.size();

            Assert.assertEquals(updatedCount, initialCount - 1, "Item was not removed from watchlist");
            System.out.println("Successfully removed one item from watchlist");
        }
    }
}