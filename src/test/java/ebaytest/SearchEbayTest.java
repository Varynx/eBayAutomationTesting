package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class SearchEbayTest extends BaseTest {

    @BeforeMethod
    public void navigateToHome() {
        driver.get("https://www.ebay.com");
    }

    // Verifies that searching a valid keyword returns results
    @Test(priority = 1)
    public void testSearchReturnsResults() throws InterruptedException
    {
        // Entering 'laptop' into the search bar
        WebElement searchInput = driver.findElement(By.id("gh-ac"));
        searchInput.sendKeys("laptop");

        WebElement searchEnter = driver.findElement(By.id("gh-search-btn"));
        searchEnter.click();

        // Collecting all the product titles into a List and verifying at least one element exists
        List<WebElement> results = driver.findElements(By.cssSelector("div.s-card__title"));
        Assert.assertFalse(results.isEmpty(), "No results for 'laptop'");

        // Will filter through the results after the search to identify valid results via the keyword "laptop"
        int matchingResults = 0;
        for(WebElement result: results)
        {
            String text  = result.getText().toLowerCase();
            if(text.contains("laptop"))
            {
                matchingResults++;
            }
        }

        Assert.assertTrue(matchingResults > 0, "No results contain 'laptop'");
    }

    // Verifies that searching an empty string does not crash or navigate away
    @Test(priority = 2)
    public void testEmptySearchQuery()
    {
        // Finding the search button and clicking without typing in search bar
        WebElement searchEnter = driver.findElement(By.id("gh-search-btn"));
        searchEnter.click();

        // Collecting the current URL
        String currentURL = driver.getCurrentUrl();

        // Collects the phrase that should show up when entering nothing
        WebElement expectedPhrase = driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[1]/div/div/div[2]/div[1]/h1"));
        String expectedText = expectedPhrase.getText();

        // Verifying that the URL is still at ebay, and that the expected phrase appears
        Assert.assertTrue(currentURL.contains("ebay.com") && expectedText.contains("All Categories"), "Empty search caused unexpected navigation");
    }

    // Verifies that search results contain the keyword in their titles
    @Test(priority = 3)
    public void testSearchNoResults()
    {
        // Entering a random string and submitting to the search bar
        WebElement searchInput = driver.findElement(By.id("gh-ac"));
        searchInput.sendKeys("bewiwoadlkvjewoaweafdjfd");

        WebElement searchEnter = driver.findElement(By.id("gh-search-btn"));
        searchEnter.click();

        // Extracting the expected text upon a random search
        WebElement expectedPhraseIdentify = driver.findElement(By.xpath("//*[@id=\"srp-river-results\"]/ul/li[2]/div/div[1]/h3"));
        String expectedText = expectedPhraseIdentify.getText();

        // If the page has the expected phrase for this behavior, pass
        Assert.assertTrue(expectedText.contains("No exact matches found"), "Results found for searched keyword 'bewiwoadlkvjewoaweafdjfd'");
    }

    // Verifies that search suggestions appear while typing
    @Test(priority = 4)
    public void testSearchSuggestionsAppear() throws InterruptedException
    {
        // Entering a partial input to allow suggestions to appear
        WebElement searchInput = driver.findElement(By.id("gh-ac"));
        searchInput.click();  // required to simulate user behavior to show suggestions, otherwise will be empty
        Thread.sleep(1000);
        searchInput.sendKeys("lap");

        Thread.sleep(1000);

        // Collect all the suggestions that are displayed
        List<WebElement> suggestions = driver.findElements(By.cssSelector("li[role='option']"));

        // Assert that the List is not empty, implying suggestions were truly shown to the user
        Assert.assertFalse(suggestions.isEmpty(), "No suggestions for 'lap'");


        // Will filter through every suggestion and ensure they all contain the keyword "laptop"
        int matchingResults = 0;
        for(WebElement s : suggestions)
        {
            String text  = s.getText().toLowerCase();
            if(text.contains("laptop"))
            {
                matchingResults++;
            }
        }

        Thread.sleep(1000);

        Assert.assertTrue(matchingResults > 0, "Search suggestions did not appear for 'laptop'");
    }

    // Verifies that a search with special characters does not throw an error
    @Test(priority = 5)
    public void testSearchWithSpecialCharacters() throws InterruptedException {

        // Entering '!@#$%' or "!@#$%dfjak@+_+" into the search bar to test TWO edge cases
        WebElement searchInput = driver.findElement(By.id("gh-ac"));
        searchInput.sendKeys("!@#$%dfjak@+_+");

        WebElement searchEnter = driver.findElement(By.id("gh-search-btn"));
        searchEnter.click();

        Thread.sleep(2000);
        // Collects the phrase for no matches edge case 1
        List<WebElement> noExactMatches = driver.findElements(By.cssSelector("h3.srp-save-null-search__heading"));

        // Collects the phrase for the empty result edge case 2
        List<WebElement> noResults = driver.findElements(By.cssSelector("div.all-cats-container h1"));

        // Conditional element handling for two edge cases
        if(!noExactMatches.isEmpty())
        {
            // Extracting the message for the first edge case
            WebElement noExactMatchMsg = noExactMatches.get(0);

            // Confirming that the message exists, then confirming that the correct phrase is displayed for this edge case
            Assert.assertTrue(noExactMatchMsg.isDisplayed(), "No message is displayed");
            Assert.assertTrue(noExactMatchMsg.getText().contains("No exact matches found"), "'No exact matches found' message not displayed!");
        }

        else if(!noResults.isEmpty())
        {
            // Extracting the message for the second edge case
            WebElement noResultMsg = noResults.get(0);

            // Confirming that the message exists, then confirming that the correct phrase is displayed for this edge case
            Assert.assertTrue(noResultMsg.isDisplayed(), "No message is displayed");
            Assert.assertTrue(noResultMsg.getText().contains("All Categories"), "'All Categories' message not displayed!");
        }

        // On the chance that neither edge case is shown, throw a fail in response
        else {
            Assert.fail("Neither edge case handled, unexpected response");
        }
    }
}
