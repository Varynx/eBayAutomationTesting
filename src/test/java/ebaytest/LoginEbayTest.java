package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;


public class LoginEbayTest extends BaseTest {
    // Declaring stable URL and user login information
    private static final String LOGIN_URL = "https://signin.ebay.com/signin";

    // Replace with another valid eBay credential as needed
    private static final String VALID_EMAIL = "";
    private static final String VALID_PASSWORD = "";


    // Simulates human pause between actions, used interchangeably with Thread.sleep(ms)
    private void humanDelay() throws InterruptedException {
        Random random = new Random();
        int delay = 500 + random.nextInt(1500); // random delay between 0.5s - 2s
        Thread.sleep(delay);
    }

    // Simulates the pauses between each character when entering text
    private void humanType(WebElement element, String text) throws InterruptedException {
        Random random = new Random();
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            Thread.sleep(50 + random.nextInt(100)); // 50-150ms per character
        }
    }

    @BeforeMethod
    public void navigateToLogin() {
        driver.manage().deleteAllCookies();
        driver.get(LOGIN_URL);
    }

    // Verifies that a user can log in with valid credentials
    @Test(priority = 1)
    public void testSuccessfulLogin() throws InterruptedException {

        humanDelay();
        WebElement enterEmail = driver.findElement(By.id("userid"));
        enterEmail.sendKeys(VALID_EMAIL);
        //humanType(enterEmail, VALID_EMAIL);

        humanDelay();

        WebElement signInContinue = driver.findElement(By.id("signin-continue-btn"));
        signInContinue.click();

        humanDelay();

        WebElement passwordInput = driver.findElement(By.id("pass"));
        passwordInput.sendKeys(VALID_PASSWORD);
        //humanType(passwordInput, VALID_PASSWORD);

        WebElement submitLogin = driver.findElement(By.id("sgnBt"));
        submitLogin.click();

        WebElement detectMainPage = driver.findElement(By.xpath("//*[@id=\"gh\"]/nav/div[1]/span[1]/div/button/span"));
        String homePageGreeting = detectMainPage.getText().toLowerCase();

        Assert.assertTrue(homePageGreeting.contains("hi"), "User is not logged in");
    }

    // Verifies that an error message appears for invalid credentials
    @Test(priority = 2)
    public void testInvalidCredentials() throws InterruptedException
    {

        Thread.sleep(500);
        WebElement enterEmail = driver.findElement(By.id("userid"));
        enterEmail.sendKeys(VALID_EMAIL);

        Thread.sleep(700);
        WebElement signInContinue = driver.findElement(By.id("signin-continue-btn"));
        signInContinue.click();

        Thread.sleep(200);
        WebElement passwordInput = driver.findElement(By.id("pass"));
        passwordInput.sendKeys("thisisaninvalidpassword123");

        humanDelay();
        WebElement submitLogin = driver.findElement(By.id("sgnBt"));
        submitLogin.click();

        WebElement errorMsg = driver.findElement(By.id("signin-error-msg"));

        Assert.assertTrue(errorMsg.isDisplayed(), "Error message not displayed for invalid credentials");
    }

    // Verifies that submitting an empty email shows a validation error
    @Test(priority = 3)
    public void testEmptyEmailValidation() throws InterruptedException {
        Thread.sleep(500);
        WebElement signInContinue = driver.findElement(By.id("signin-continue-btn"));
        signInContinue.click();

        Thread.sleep(700);
        WebElement errorMsg = driver.findElement(By.id("signin-error-msg"));
        String errorMsgText = errorMsg.getText();
        Assert.assertTrue(errorMsgText.contains("Oops"), "Validation error not shown for empty email");
    }

    // Verifies that the forgot password link navigates to the reset page
    @Test(priority = 4)
    public void testForgotPasswordLinkIsDisplayed() throws InterruptedException
    {
        Thread.sleep(500);
        WebElement enterEmail = driver.findElement(By.id("userid"));
        enterEmail.sendKeys(VALID_EMAIL);

        Thread.sleep(700);
        WebElement signInContinue = driver.findElement(By.id("signin-continue-btn"));
        signInContinue.click();

        Thread.sleep(200);
        WebElement resetPassword = driver.findElement(By.id("need-help-signin-link"));
        String resetPasswordText = resetPassword.getText();

        // Guaranteed captcha, verifying the Reset button exists instead
        /*resetPassword.click();
        Thread.sleep(400);
        WebElement errorMsg = driver.findElement(By.id("hub-intro-header"));
        String errorMsgText = errorMsg.getText();*/

        Assert.assertTrue(resetPasswordText.contains("Reset your password"), "Did not navigate to forgot password page");
    }

    // Verifies that the user can log out successfully after logging in
    @Test(priority = 5)
    public void testLogout() throws InterruptedException {
        testSuccessfulLogin();

        Thread.sleep(300);
        Actions actions = new Actions(driver);
        WebElement greetingDropDown = driver.findElement(By.xpath("//*[@id=\"gh\"]/nav/div[1]/span[1]/div/button"));

        actions.moveToElement(greetingDropDown).build().perform();

        WebElement signOutButton = driver.findElement(By.linkText("Sign out"));
        signOutButton.click();

        WebElement verifySignOutMsg = driver.findElement(By.id("signout-banner-text"));
        String verifySignOutMsgText = verifySignOutMsg.getText();

        Assert.assertTrue(verifySignOutMsgText.contains("signed out"), "User is still logged in after logout");
    }
}
