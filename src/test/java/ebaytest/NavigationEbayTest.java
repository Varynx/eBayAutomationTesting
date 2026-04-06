package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class NavigationEbayTest extends BaseTest {

    @BeforeMethod
    public void navigateToHome() {
        driver.get("https://www.ebay.com");
    }

    // Verifies that the eBay logo navigates back to the home page
    @Test(priority = 1)
    public void testLogoNavigatesToHome() {
        // Navigate away from home first (directly start at 'Deals')
        driver.get("https://www.ebay.com/deals");

        // Identify and click the eBay logo
        WebElement eBayLogo = driver.findElement(By.id("gh-logo"));
        eBayLogo.click();

        // Identify the expected URL and message

        WebElement signedOutText = driver.findElement(By.xpath("//*[@id=\"gh\"]/nav/div[1]/span[1]/span"));
        String signedOutTextMsg = signedOutText.getText();

        String currentURL = driver.getCurrentUrl();

        // Evaluate the behavior to verify Home page navigation
        Assert.assertTrue(signedOutTextMsg.contains("Hi!") && currentURL.contains("https://www.ebay.com/"), "Unexpected behavior, user not at home page");
    }

    // Verifies that clicking a category from the nav bar loads the correct page
    @Test(priority = 2)
    public void testCategoryNavigation() throws InterruptedException
    {
        // Gathering the categories to test, avoiding navigation that requires log ins
        List<String> targetCategories = List.of(
                "Motors",
                "Electronics",
                "Collectibles",
                "Home and garden",
                "Clothing, shoes and accessories",
                "Toys"
        );

        // Gathering the categories into a WebElement list
        List<WebElement> categories = driver.findElements(By.cssSelector("li.vl-flyout-nav__js-tab"));
        // Confirming the above list is not empty (error checking)
        Assert.assertFalse(categories.isEmpty(), "Categories List is empty");


        for(int i = 0; i < categories.size(); i++)
        {
            // Re-fetching on each iteration to avoid StaleElementReferenceException (since the page constantly refreshes/navigates to fresh page)
            categories = driver.findElements(By.cssSelector("li.vl-flyout-nav__js-tab"));
            WebElement category = categories.get(i);

            String categoryText = category.getText().trim();

            // Skip anything not in the List of links to click
            if(!targetCategories.contains(categoryText))
            {
                // Explicitly showing which links that was NOT clicked
                System.out.println("Skipping: " + categoryText);
                continue;
            }

            category.click();
            Thread.sleep(2000);

            String currentURL = driver.getCurrentUrl();

            System.out.println(currentURL);

            Assert.assertFalse(currentURL.isEmpty(), "Current URL is empty");

            // Redirecting the browser back to the home page to test the next category
            driver.get("https://www.ebay.com");
            Thread.sleep(2000);
        }
    }
    // Verifies category sub sections show valid results
    @Test(priority = 3)
    public void testShopByCategoryNavigation() throws InterruptedException {

        // Identifying the 'Shop by Categories' button, will create separate elements for the same action due to avoid StaleElementReferenceException
        WebElement shopByCategoryButton = driver.findElement(By.xpath("//*[@id=\"gh\"]/section/div/div/div/button"));
        shopByCategoryButton.click();

        Thread.sleep(500);

        // Choosing three random subsections from three random categories

        // Clicking 'Collectible Sneakers' under 'Clothing & Accessories'
        WebElement sneakersButton = driver.findElement(By.linkText("Collectible Sneakers"));
        sneakersButton.click();
        Thread.sleep(500);
        String sneakersURL = driver.getCurrentUrl();

        // Choosing another options
        WebElement shopByCategoryButton2 = driver.findElement(By.xpath("//*[@id=\"gh\"]/section/div/div/div/button"));
        shopByCategoryButton2.click();

        Thread.sleep(500);

        // Clicking 'Home Improvement' under 'Home & Garden'
        WebElement homeImprovementButton = driver.findElement(By.linkText("Home Improvement"));
        homeImprovementButton.click();
        Thread.sleep(500);
        String homeImprovementURL = driver.getCurrentUrl();

        // Choosing another option
        WebElement shopByCategoryButton3 = driver.findElement(By.xpath("//*[@id=\"gh\"]/section/div/div/div/button"));
        shopByCategoryButton3.click();

        WebElement luxuryWatchesButton = driver.findElement(By.linkText("Luxury Watches"));
        luxuryWatchesButton.click();
        Thread.sleep(500);
        String luxuryWatchesURL = driver.getCurrentUrl();

        Assert.assertTrue(sneakersURL.contains("Collectible-Sneakers") &&
                homeImprovementURL.contains("Home-Improvement") &&
                luxuryWatchesURL.contains("Luxury-Watches"),"At least one of the sub sections did not apply a correct response");
    }

    // Verifies that the Deals, Brand Outlet, and Gift Cards  link in the header navigates correctly
    @Test(priority = 4)
    public void testHeaderNavigation() throws InterruptedException
    {
        // Clicks the links in the header for all three links
        WebElement dealsButton = driver.findElement(By.linkText("Deals"));
        dealsButton.click();

        // Assert true by checking the URL of the website
        String firstURL = driver.getCurrentUrl();
        Assert.assertTrue(firstURL.contains("deals"), "Current URL is empty, unexpected response");

        Thread.sleep(2000);
        // Redirecting the browser back to the home page to test the next header
        driver.get("https://www.ebay.com");

        WebElement brandOutletButton = driver.findElement(By.linkText("Brand Outlet"));
        brandOutletButton.click();

        String secondURL = driver.getCurrentUrl();
        Assert.assertTrue(secondURL.contains("Brand-Outlet"), "Current URL is empty, unexpected response");

        Thread.sleep(2000);
        driver.get("https://www.ebay.com");

        WebElement giftCardButton = driver.findElement(By.linkText("Gift Cards"));
        giftCardButton.click();

        String thirdURL = driver.getCurrentUrl();
        Assert.assertTrue(thirdURL.contains("giftcards"), "Current URL is empty, unexpected response");
    }

    // Verifies that the Help & Contact link in the header navigates correctly, will simulate a user interaction
    @Test(priority = 5)
    public void testHelpAndContactNavigation() throws InterruptedException
    {
        // Identifying and clicking the Help button at the header\

        Actions actions = new Actions(driver);
        WebElement helpButton = driver.findElement(By.linkText("Help & Contact"));

        Thread.sleep(500);

        actions.moveToElement(helpButton).perform();
        helpButton.click();

        Thread.sleep(2000);

        // Identifying and clicking the 'Contact Us' button
        WebElement contactButton = driver.findElement(By.id("recommend_contact_us"));
        js.executeScript("arguments[0].scrollIntoView(true);", contactButton);
        Thread.sleep(2000);
        contactButton.click();

        Thread.sleep(500);

        // Choosing the 'Account' option for help
        WebElement accountButton = driver.findElement(By.xpath("//*[@id=\"options\"]/a[3]"));
        accountButton.click();

        Thread.sleep(500);

        // Choosing 'Creating an eBay account' to send user to sign in screen
        WebElement createAccountButton = driver.findElement(By.xpath("//*[@id=\"options\"]/a[1]/span"));
        createAccountButton.click();

        Thread.sleep(500);

        // Identify website elements that verify sign in page
        WebElement greetingText = driver.findElement(By.id("greeting-msg"));
        String greetingMsg = greetingText.getText();

        String currentURL = driver.getCurrentUrl();

        Assert.assertTrue(greetingMsg.contains("Sign in to your account") && currentURL.contains("signin.ebay.com"), "Unexpected behavior, user not at sign in page");
    }

    // Verifies that the browser back button returns to the previous page correctly
    @Test(priority = 6)
    public void testBrowserBackNavigation() throws InterruptedException {
        // Navigate to deals page
        WebElement dealsButton  = driver.findElement(By.linkText("Deals"));
        dealsButton.click();
        String dealsURL = driver.getCurrentUrl();

        Thread.sleep(500);
        // Navigate to brand outlet page
        WebElement brandOutletButton = driver.findElement(By.linkText("Brand Outlet"));
        brandOutletButton.click();
        String brandOutletURL = driver.getCurrentUrl();

        Thread.sleep(500);
        // Navigate to gift cards page
        WebElement giftCardButton = driver.findElement(By.linkText("Gift Cards"));
        giftCardButton.click();

        Thread.sleep(500);
        // Go back to brand outlet and verify
        driver.navigate().back();
        Assert.assertEquals(driver.getCurrentUrl(), brandOutletURL, "Unexpected behavior, user not at brand page");

        Thread.sleep(500);

        driver.navigate().back();
        Assert.assertEquals(driver.getCurrentUrl(),dealsURL, "Unexpected behavior, user not at deals page");
    }


}