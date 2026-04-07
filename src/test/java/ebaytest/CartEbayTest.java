package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class CartEbayTest extends BaseTest {

    // Change 'product-id' to true product id as needed, assumption that there are n > 1 products available
    private static final String ITEM_URL = "https://www.ebay.com/itm/product-id";
    private static final String CART_URL = "https://cart.ebay.com/";

    // Gets the URL of the current product, will be used as the basis for each test case since they run sequentially
    @BeforeMethod
    public void navigateToItem() {
        driver.get(ITEM_URL);
    }

    // Verifies that an item can be added to the cart from a listing page
    @Test(priority = 1)
    public void testAddItemToCart() throws InterruptedException
    {
        Thread.sleep(2000);
        // Identifying "Add to Cart" button and clicking
        WebElement addToCartButton = driver.findElement(By.xpath("//*[@id=\"atcBtn_btn_1\"]/span/span"));
        addToCartButton.click();

        Thread.sleep(1000);

        WebElement confirmAddMsg = driver.findElement(By.xpath("//*[@id=\"s0-2-1-25-5-17-1-43[6]-2[1]-2-0-@dialog-dialog-title\"]/div/span"));
        String confirmAddMsgText = confirmAddMsg.getText();

        Assert.assertTrue(confirmAddMsgText.contains("Added to cart"),"Item was not added to cart");
    }

    // Verifies that the cart correctly shows the item that was added
    @Test(priority = 2)
    public void testCartDisplaysAddedItem() throws InterruptedException
    {
        // Starts from the previous method, product already included
        driver.get(CART_URL);

        Thread.sleep(2000);

        // Extract the text from the current URL, to check for "Order summary"
        WebElement orderSummary = driver.findElement(By.cssSelector("h2.order-summary-title"));
        String orderSummaryText = orderSummary.getText();

        Thread.sleep(2000);
        // Create a List of products that are in the cart
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart-item"));

        Thread.sleep(1000);

        // Check if the cart is empty AND there is no summary text is FALSE, implying they exist
        Assert.assertFalse(cartItems.isEmpty() && orderSummaryText.isBlank(), "Cart is empty after adding an item");
    }

    // Verifies that updating the item quantity reflects correctly in the cart
    @Test(priority = 3)
    public void testUpdateItemQuantity() throws InterruptedException {
        driver.get(CART_URL);

        Thread.sleep(1000);
        // Identify the '+' button and click
        WebElement qtyIncrease = driver.findElement(By.cssSelector(".number-input__increment"));
        qtyIncrease.click();

        // Identify the current number displayed
        WebElement updatedQty = driver.findElement(By.cssSelector("input.textbox__control"));
        String updatedQtyText = updatedQty.getAttribute("value");

        Thread.sleep(1000);
        // Assert that the quantity was increased by one (i.e., says '2')
        Assert.assertEquals(updatedQtyText, "2","Item was not updated");
    }

    // Verifies that the cart total updates when quantity changes
    @Test(priority = 4)
    public void testCartTotalUpdatesWithQuantity() throws InterruptedException
    {
        driver.get(CART_URL);

        Thread.sleep(1000);

        // Decrement the counter to get the "initial subtotal" for a given product
        WebElement qtyDecrease = driver.findElement(By.cssSelector(".number-input__decrement"));
        qtyDecrease.click();

        Thread.sleep(1000);

        // Extract the number for the initial total
        String initialTotal = driver.findElement(By.xpath("//*[@id=\"mainContent\"]" +
                "/div[1]/div[3]/div[2]/div/div/div[1]/div[4]/div[2]")).getText();

        // Increment the counter to get the "final subtotal" for a given product
        WebElement qtyIncrease = driver.findElement(By.cssSelector(".number-input__increment"));
        qtyIncrease.click();

        Thread.sleep(1000);

        // Extract the number for the final total
        String finalTotal = driver.findElement(By.xpath("//*[@id=\"mainContent\"]" +
                "/div[1]/div[3]/div[2]/div/div/div[1]/div[4]/div[2]")).getText();

        System.out.println(initialTotal);
        System.out.println(finalTotal);

        // Compares the initial and final to check that they are not equal, implying a change to the subtotal
        Assert.assertNotEquals(initialTotal, finalTotal, "Cart total did not change after quantity update");
    }
    // Verifies that an item can be removed from the cart
    @Test(priority = 5)
    public void testRemoveItemFromCart() throws InterruptedException {
        driver.get(CART_URL);

        // Identifying all items in the cart and removing them
        List<WebElement> removeBtns = driver.findElements(By.xpath("//*[@id=\"mainContent\"]" +
                "/div[1]/div[3]/div[1]/div/div/ul/li/div[1]/div/div/div/div/div[2]/div[3]/div[2]/span[2]/button"));

        for (WebElement removeBtn : removeBtns) {
            removeBtn.click();
        }

        Thread.sleep(1000);
        // Identifying all items current in the cart and evaluating its existence
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart-item"));

        Thread.sleep(1000);
        Assert.assertTrue(cartItems.isEmpty(), "Cart still has items after removal");
    }

}
