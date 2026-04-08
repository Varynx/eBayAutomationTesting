package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.io.FileHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProductEbayTest extends BaseTest {

    @BeforeMethod
    public void before() throws InterruptedException {

        // Navigate to product
        driver.get("https://www.ebay.com/itm/366231173654 ");
        Thread.sleep(500);
    }

    // Verifies that product images can be navigated through the gallery
    @Test(priority = 1)
    public void testImageGallery() throws InterruptedException, IOException {


        // Locate the image gallery and take a screenshot
        WebElement openGallery = driver.findElement(By.cssSelector("button[aria-label='Opens image gallery']"));
        openGallery.click();
        Thread.sleep(500);

        // Check the amount of images that should be shown, printed to output
        WebElement numberOfImages = driver.findElement(By.cssSelector(".x-photos-max-view-gallery-title"));
        String maxNumImages = numberOfImages.getText().replaceAll(
                ".*of\\s+(\\d+).*", "$1"); //ignore, find 'of', capture number
        System.out.println("Number of Images that should appear in the screenshot: " + maxNumImages);
        Thread.sleep(1500);

        // Take a screenshot
        TakesScreenshot ts = (TakesScreenshot) driver; // screenshot object
        File screenshot = ts.getScreenshotAs(OutputType.FILE); // capture the screenshot as a file

        File screenshotsDir = new File("target/screenshots");
        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdirs(); // Creates the directory if missing
        }

        File destFile = new File("target/screenshots/productImages.png");// destination
        FileHandler.copy(screenshot, destFile);
        Thread.sleep(2000);


    }


    // Verifies that seller information is displayed correctly
    @Test(priority = 2)
    public void testSellerInformationDisplay() throws InterruptedException {

        // Show information about the seller
        WebElement moreSellerInfo = driver.findElement(
                By.cssSelector("button[aria-label='See more about this seller']"));
        moreSellerInfo.click();
        Thread.sleep(1000);

        // Locate the main container to be used to check the elements for seller info
        WebElement storeSection = driver.findElement(
                By.cssSelector("div[data-testid='x-store-information']"));

        // Check there is a store name
        String storeName = storeSection.findElement(By.cssSelector(
                ".x-store-information__store-name span.ux-textspans--BOLD")).getAttribute("textContent");
        System.out.println("Store Name: " + storeName);
        Assert.assertFalse(storeName.isEmpty(), "Store name should not be empty");

        // Check for feedback and items sold
        String feedback = storeSection.findElement(By.cssSelector(
                ".x-store-information__highlights .ux-textspans:first-child")).getAttribute("textContent");
        String itemsSold = storeSection.findElement(By.cssSelector(
                ".x-store-information__highlights .ux-textspans:nth-child(3)")).getAttribute("textContent");

        System.out.println("Feedback: " + feedback);
        System.out.println("Items Sold: " + itemsSold);

        Assert.assertTrue(feedback.contains("positive"), "Feedback should contain 'positive'");
        Assert.assertTrue(itemsSold.matches(".*\\d.*"), "Items sold should contain a number");

        // Check for a join date
        String joinDate = storeSection.findElement( By.cssSelector(
                ".ux-icon-text__text .ux-textspans")).getAttribute("textContent");
        System.out.println("Join Date: " + joinDate);
        Assert.assertTrue(joinDate.startsWith("Joined"), "Join date should start with 'Joined'");

        // Check for a Bio
        String bio = storeSection.findElement(By.cssSelector(
                ".x-store-information__bio [data-testid='text']")).getAttribute("textContent");
        System.out.println("Bio: " + bio);
        Assert.assertFalse(bio.isEmpty(), "Bio should not be empty");
    }


    // Checks the shipping, returns, and payment details are correct
    @Test(priority = 3)
    public void testShippingReturnsPaymentDisplay() throws InterruptedException {
        // Show information about shipping, returns, and payment details
        WebElement seeDetails = driver.findElement(By.xpath(
                "//button[contains(@class,'fake-link--action')]//span[contains(text(),'See details')]"));
        seeDetails.click();
        Thread.sleep(1000);

        // Input FGCU zip code
        WebElement zipInput = driver.findElement(By.id("shZipCode"));
        zipInput.clear();
        zipInput.sendKeys("33965");
        WebElement updateButton = driver.findElement(By.xpath("//button[text()='Update']"));
        updateButton.click();

        // Scroll to the shipping section
        WebElement shippingSection = driver.findElement(By.cssSelector(".x-shipping-maxview"));
        js.executeScript("arguments[0].scrollIntoView(true);", shippingSection);
        Thread.sleep(750);

        // Verify shipping cost is displayed
        WebElement shippingCost = driver.findElement(By.xpath(
                "//div[contains(@class,'ux-labels-values--deliveryto')]//span[contains(text(),'US $')]"));
        String shippingPrice = shippingCost.getAttribute("textContent").trim();
        System.out.println("Shipping Cost: " + shippingPrice);
        Assert.assertTrue(shippingPrice.contains("US $"), "Shipping cost should display a price");
        Thread.sleep(750);

//        // Verify estimated delivery dates
//        WebElement deliveryDate = driver.findElement(By.xpath("//*[@id=\"s0-2-1-25-5-23-5[0]-136[0]-2-0-@dialog-16-1-10-6-2-3-tabpanel-0\"]" +
//                        "/div/div/div/div[1]/div/div/div[3]/div/div[3]/div/div/div/div[2]/div/div[2]"));
//        String deliveryText = deliveryDate.getAttribute("textContent").trim();
//        System.out.println("Delivery Estimate: " + deliveryText);
//        Assert.assertTrue(deliveryText.contains("Get it between"), "Delivery estimate should be displayed");

        // Verify item location
        WebElement itemLocation = driver.findElement(By.cssSelector(
                ".ux-labels-values--itemLocation .ux-labels-values__values-content span"));
        String location = itemLocation.getAttribute("textContent").trim();
        System.out.println("Item Location: " + location);
        Assert.assertFalse(location.isEmpty(), "Item location should not be empty");

        // Scroll to returns section
        WebElement returnsSection = driver.findElement(By.cssSelector(".x-returns-maxview"));
        js.executeScript("arguments[0].scrollIntoView(true);", returnsSection);
        Thread.sleep(500);

        // Verify return policy timeframe
        WebElement returnPolicy = driver.findElement(By.cssSelector(
                ".ux-section--returns .ux-labels-values__values-content div:nth-child(1)"));
        String returnText = returnPolicy.getText().trim();
        System.out.println("Return Policy: " + returnText);
        Assert.assertFalse(returnText.isEmpty(), "Return policy should be displayed");

        // Verify return shipping responsibility
        WebElement returnShipping = driver.findElement(By.cssSelector(
                ".ux-section--returns .ux-section__item:nth-child(2) .ux-labels-values__values-content span"));
        String shippingResponsibility = returnShipping.getText().trim();
        System.out.println("Return Shipping: " + shippingResponsibility);
        Assert.assertFalse(shippingResponsibility.isEmpty(), "Return shipping responsibility should be displayed");

        // Click on Payments tab to view payment methods
        WebElement paymentsTab = driver.findElement(By.xpath(
                "//span[text()='Payment methods']/parent::div[@role='tab']"));
        paymentsTab.click();
        Thread.sleep(1000);

        // Verify payment methods section is displayed
        WebElement paymentMethods = driver.findElement(By.cssSelector(".ux-section--paymentmethods"));
        Assert.assertTrue(paymentMethods.isDisplayed(), "Payment methods section should be displayed");

        // Verify at least one payment method icon is present
        List<WebElement> paymentIcons = paymentMethods.findElements(By.cssSelector(
                "span[role='img'], .ux-payment-icon"));
        System.out.println("Number of payment methods: " + paymentIcons.size());
        Assert.assertTrue(paymentIcons.size() > 0, "At least one payment method should be displayed");
    }

    // Verifies that item specifications table is displayed
    @Test(priority = 4)
    public void testItemSpecificationsDisplay() throws InterruptedException {


        // Scroll to the tabs content
        WebElement tabsContent = driver.findElement(By.cssSelector(".tabs__content"));
        js.executeScript("arguments[0].scrollIntoView(true);", tabsContent);
        Thread.sleep(500);

        // Locate the "Item specifics" section
        WebElement itemSpecifics = tabsContent.findElement(By.cssSelector(".x-about-this-item"));
        js.executeScript("arguments[0].scrollIntoView(true);", itemSpecifics);
        Thread.sleep(500);

        // Verify section is displayed
        Assert.assertTrue(itemSpecifics.isDisplayed(), "Item specifications section is not displayed");

        // Condition
        WebElement conditionLabel = itemSpecifics.findElement(By.cssSelector(".ux-labels-values--condition .ux-labels-values__labels-content"));
        WebElement conditionValue = itemSpecifics.findElement(By.cssSelector(".ux-labels-values--condition .ux-labels-values__values-content"));
        Assert.assertEquals(conditionLabel.getText().trim(), "Condition");
        System.out.println("Condition: " + conditionValue.getText().trim());

        // Product
        WebElement productValue = itemSpecifics.findElement(By.cssSelector(".ux-labels-values--product .ux-labels-values__values-content"));
        System.out.println("Product: " + productValue.getText().trim());
    }

    // Verifies that price and availability are displayed correctly
    @Test(priority = 5)
    public void testPriceAndAvailability() {

        // Verify price is displayed
        WebElement price = driver.findElement(By.cssSelector("div.x-price-primary span.ux-textspans"));
        String priceText = price.getAttribute("textContent");
        System.out.println("Price: " + priceText);
        Assert.assertTrue(priceText.startsWith("US $"), "Price should start with 'US $'");

        // Verify quantity box is displayed
        WebElement qtyBox = driver.findElement(By.cssSelector("#qtyTextBox"));
        System.out.println("Quantity Box Displayed: " + qtyBox.isDisplayed());
        Assert.assertTrue(qtyBox.isDisplayed(), "Quantity input should be visible");

        // Verify stock / availability text
        WebElement stockInfo = driver.findElement(By.cssSelector(
                "div.x-quantity__availability span.ux-textspans"));
        String stockText = stockInfo.getAttribute("textContent");
        System.out.println("Stock Info: " + stockText);
        Assert.assertFalse(stockText.isEmpty(), "Stock information should be displayed");
    }

}