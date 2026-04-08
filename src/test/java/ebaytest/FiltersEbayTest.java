package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class FiltersEbayTest extends BaseTest {

    @BeforeMethod
    public void searchForItem() {

        // Gets page before method in order to clear consistent filters
        driver.get("https://www.ebay.com/sch/i.html?_nkw=laptop");
    }


    // Verifies that filtering by 'Buy It Now' updates the page results
    @Test(priority = 1)
    public void testBuyItNowFilter() throws InterruptedException {

        Thread.sleep(1000);

        // The 'Buy It Now' in the bar above the items is located and clicked
        List<WebElement> tabs = driver.findElements(By.cssSelector(".srp-format-tabs-h2"));
        WebElement buyItNowTab = tabs.get(2);
        buyItNowTab.click();

        Thread.sleep(1000);

        // Asserts the url as containing the correct text in order for it to be showing the filtered tab
        String currentURL = driver.getCurrentUrl();
        Assert.assertTrue(currentURL.contains("LH_BIN=1"), "The 'Buy It Now' filter was not applied to the page");
    }


    // Verifies that filtering by 'New' condition returns only new items
    @Test(priority = 2)
    public void testConditionNewFilter() throws InterruptedException {

        Thread.sleep(1000);

        // The 'New' checkbox is located and ticked off
        WebElement newCheckbox = driver.findElement( By.cssSelector("input.checkbox__control[aria-label=\"New\"]") );
        js.executeScript("arguments[0].scrollIntoView(true);", newCheckbox);
        Thread.sleep(1000);
        newCheckbox.click();

        // Puts all the elements in the current page into the list
        List<WebElement> conditions = driver.findElements(By.cssSelector(".s-item__subtitle"));

        // Loops through the current page's element and checks if they are new
        boolean allNew = true;
        for (WebElement element : conditions) {
            String text = element.getText().toLowerCase();
            // either contains 'Brand New' or is empty(for elements that aren't listings)
            if (!text.contains("Brand New") && !text.isEmpty()) {
                allNew = false;
                break; // stop when one doesn't match
            }
        }

        // Asserts the all the elements were new
        Assert.assertTrue(allNew, "Some items are not listed as 'New' after applying condition filter");
    }


    // Verifies that setting a max price filters out higher-priced results
    @Test(priority = 3)
    public void testPriceRangeFilter() throws InterruptedException {

        // Enter the max price for the search results
        WebElement maxPrice = driver.findElement(By.cssSelector("input[aria-label=\"Maximum Value in $\"]") );
        maxPrice.sendKeys("500");
        Thread.sleep(1000);

        WebElement resultsButton = driver.findElement(By.cssSelector("button[aria-label='Submit price range']"));
        resultsButton.click();

        // Checks the Free Shipping option in order to not include the insane shipping price listings
        WebElement freeShipping = driver.findElement(By.cssSelector("input[aria-label='Free Shipping']"));
        freeShipping.click();

        // Sort by highest price
        WebElement sortDropdown = driver.findElement(By.cssSelector("button[aria-label='Sort']"));
        sortDropdown.click();
        Thread.sleep(500);

        WebElement highestPriceOption = driver.findElement(By.xpath("//span[text()='Price + Shipping: highest first']"));
        highestPriceOption.click();
        Thread.sleep(1000);

        // Get all price elements and verify descending order
        List<WebElement> priceElements = driver.findElements(By.cssSelector(".s-item__price"));

        // Intial comparison for the elements
        double previousPrice = Double.MAX_VALUE;
        boolean descendingOrder = true;

        for (WebElement element : priceElements) {
            // Extract only numbers and decimals from the price text, removes $, commas, etc.
            String priceText = element.getText().replaceAll("[^0-9.]", "");

            // Skips checking elements that didn't have any text
            if (!priceText.isEmpty()) {

                // Converts string to a number
                double currentPrice = Double.parseDouble(priceText);

                // Each price should be less than or equal to the previous one (descending)
                if (currentPrice > previousPrice) {
                    descendingOrder = false;
                    break;
                }

                previousPrice = currentPrice;
            }
        }

        // Asserts that the listings were sorted in descending order
        Assert.assertTrue(descendingOrder, "Prices are not sorted from highest to lowest");
    }


    // Verifies that sorting by 'Price + Shipping: Lowest First' reorders results
    @Test(priority = 4)
    public void testSortByPriceLowest() throws InterruptedException {

        // Sort by lowest price
        WebElement sortDropdown = driver.findElement(By.cssSelector("button[aria-label='Sort']"));
        sortDropdown.click();
        Thread.sleep(500);

        WebElement lowestPriceOption = driver.findElement(By.xpath("//span[text()='Price + Shipping: lowest first']"));
        lowestPriceOption.click();
        Thread.sleep(1000);


        // Get all price elements and verify ascending order
        List<WebElement> priceElements = driver.findElements(By.cssSelector(".s-item__price"));

        // Zero to compare against first price
        double previousPrice = 0;
        boolean ascendingOrder = true;

        for (WebElement element : priceElements) {

            // Extract only numbers and decimals from the price text (removes $, commas, etc.)
            String priceText = element.getText().replaceAll("[^0-9.]", "");

            if (!priceText.isEmpty()) {
                double currentPrice = Double.parseDouble(priceText);

                // Each price should be greater than or equal to the previous one (ascending)
                if (currentPrice < previousPrice) {
                    ascendingOrder = false;
                    break;
                }

                previousPrice = currentPrice;
            }
        }

        // Asserts that the listings were sorted in ascending order
        Assert.assertTrue(ascendingOrder, "Prices are not sorted from lowest to highest");
    }


    // Verifies that filtering by 'Sold Items' shows only sold listings
    @Test(priority = 5)
    public void testSoldItemsFilter() throws InterruptedException {

        Thread.sleep(1000);

        // The 'Sold Items' checkbox is located and ticked off
        WebElement soldCheckbox = driver.findElement(By.cssSelector("input.checkbox__control[aria-label=\"Sold Items\"]"));
        js.executeScript("arguments[0].scrollIntoView(true);", soldCheckbox);
        Thread.sleep(1000);
        soldCheckbox.click();
        Thread.sleep(1000);

        // Puts all the sold status elements in the current page into the list
        List<WebElement> soldStatuses = driver.findElements(By.cssSelector(".s-item__title--tag"));

        // Loops through the current page's elements and checks if they are sold
        boolean allSold = true;
        for (WebElement element : soldStatuses) {
            String text = element.getText().toLowerCase();

            // Either contains 'sold' or is empty (for elements that aren't listings)
            if (!text.contains("sold") && !text.isEmpty()) {
                allSold = false;
                break; // stop when one doesn't match
            }
        }

        // Asserts that all the elements were sold
        Assert.assertTrue(allSold, "Some items are not listed as 'Sold' after applying sold items filter");
    }

}