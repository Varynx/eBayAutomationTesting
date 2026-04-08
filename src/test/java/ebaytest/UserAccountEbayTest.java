package ebaytest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.openqa.selenium.interactions.Actions;

import java.util.Random;

public class UserAccountEbayTest extends BaseTest {

    // Declaring stable URL and user login information
    private static final String LOGIN_URL = "https://signin.ebay.com/signin";

    // Replace with valid eBay credentials
    private static final String VALID_EMAIL = "";
    private static final String VALID_PASSWORD = ".";

    // Simulates human pause between actions, used interchangeably with Thread.sleep(ms)
    private void humanDelay() throws InterruptedException {
        Random random = new Random();
        int delay = 500 + random.nextInt(1500); // random delay between 0.5s - 2s
        Thread.sleep(delay);
    }

    @BeforeClass
    public void goToHomePage() throws InterruptedException {
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


    // Verifies that a user can successfully navigate to account settings
    @Test(priority = 1)
    public void testViewAccountSettings() throws InterruptedException {

        Actions actions = new Actions(driver);

        // Navigate to account settings
        WebElement accountMenu = driver.findElement(By.cssSelector(".gh-flyout__target.gh-flyout__target--left"));
        actions.moveToElement(accountMenu).perform();
        Thread.sleep(500);


        WebElement accountSettings = driver.findElement(By.linkText("Account settings"));
        accountSettings.click();
        Thread.sleep(1000);

        // Assert URL contains account page
        String currentURL = driver.getCurrentUrl();
        Assert.assertTrue(currentURL.contains("account"),
                "User was not navigated to account settings");

        // Checks if we are in the account tab
        WebElement accountTab = driver.findElement(By.cssSelector("a[aria-current='page']"));
        String tabText = accountTab.getText();

        Assert.assertEquals(tabText, "Account", "User is not in the correct tab");
        System.out.println("Navigated correctly to the Account page/tab");

    }


    // Verifies that all main account settings cards and key links are displayed
    @Test(priority = 2)
    public void testAccountSettingsCardsDisplayed() throws InterruptedException {

        Thread.sleep(1000);

        // Locate all cards
        java.util.List<WebElement> cards = driver.findElements(By.cssSelector(".cards-account-settings"));

        // Verify multiple cards are present
        Assert.assertTrue(cards.size() >= 5, "Not all account setting cards are displayed");

        // Verify main section headers exist
        boolean hasPersonalInfo = driver.findElement(By.xpath("//h2[text()='Personal Info']")).isDisplayed();
        boolean hasPaymentInfo = driver.findElement(By.xpath("//h2[text()='Payment Information']")).isDisplayed();
        boolean hasAccountPrefs = driver.findElement(By.xpath("//h2[text()='Account Preferences']")).isDisplayed();
        boolean hasSelling = driver.findElement(By.xpath("//h2[text()='Selling']")).isDisplayed();
        boolean hasDonations = driver.findElement(By.xpath("//h2[text()='Donation Preferences']")).isDisplayed();

        Assert.assertTrue(
                hasPersonalInfo && hasPaymentInfo && hasAccountPrefs && hasSelling && hasDonations,
                "One or more account setting sections are missing"
        );

        // Verify some important links
        WebElement personalInfoLink = driver.findElement(By.id("account-settings-link-PI"));
        WebElement securityLink = driver.findElement(By.id("account-settings-link-SIAS"));
        WebElement addressLink = driver.findElement(By.id("account-settings-link-ADDR"));
        WebElement paymentsLink = driver.findElement(By.id("account-settings-link-PO"));
        WebElement sellerAccountLink = driver.findElement(By.id("account-settings-link-SELLACC"));

        Assert.assertTrue(personalInfoLink.isDisplayed(), "Personal Info link not visible");
        Assert.assertTrue(securityLink.isDisplayed(), "Security link not visible");
        Assert.assertTrue(addressLink.isDisplayed(), "Address link not visible");
        Assert.assertTrue(paymentsLink.isDisplayed(), "Payments link not visible");
        Assert.assertTrue(sellerAccountLink.isDisplayed(), "Seller Account link not visible");

        System.out.println("All the cards are being displayed");
    }


    // Verifies that key account setting links navigate to correct pages
    @Test(priority = 3)
    public void testAccountSubLinksNavigation() throws InterruptedException {
        Thread.sleep(1000);

        // Navigate to Security/Account settings (or main Account page)
        WebElement accountSettingsLink = driver.findElement(By.linkText("Sign-in and security"));
        accountSettingsLink.click();
        Thread.sleep(2000);
        driver.navigate().back();

        // Clicks Feedback link and verifies navigation
        WebElement feedbackLink = driver.findElement(By.linkText("Feedback"));
        feedbackLink.click();
        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("feedback"),
                "Navigation failed for Feedback link");
        driver.navigate().back();
        Thread.sleep(2000);

        // Clicks Resolution Hub link and verifies navigation
        WebElement resolutionHubLink = driver.findElement(By.linkText("Resolution Hub"));
        resolutionHubLink.click();
        Thread.sleep(2000);

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("rh"),
                "Navigation failed for Resolution Hub link");
        driver.navigate().back();
        Thread.sleep(2000);

        // Clicks Seller Dashboard link and verifies navigation
        WebElement sellerDashboardLink = driver.findElement(By.linkText("Seller Dashboard"));
        sellerDashboardLink.click();
        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("dashboard"),
                "Navigation failed for Seller Dashboard link");
        driver.navigate().back();
        Thread.sleep(2000);

        System.out.println("Feedback, Resolution Hub, and Seller Dashboard links navigate correctly");
    }


    @Test(priority = 4)
    public void testPersonalInfoDetailsDisplayed() throws InterruptedException {

        Thread.sleep(1000);

        WebElement feedbackLink = driver.findElement(By.linkText("Personal information"));
        feedbackLink.click();
        Thread.sleep(750);

//        //login again, sometimes
//        WebElement enterEmail = driver.findElement(By.id("userid"));
//        enterEmail.sendKeys(VALID_EMAIL);
//        humanDelay();
//
//        WebElement signInContinue = driver.findElement(By.id("signin-continue-btn"));
//        signInContinue.click();
//        humanDelay();
//
//        WebElement passwordInput = driver.findElement(By.id("pass"));
//        passwordInput.sendKeys(VALID_PASSWORD);
//
//        WebElement submitLogin = driver.findElement(By.id("sgnBt"));
//        submitLogin.click();
//        humanDelay();
//        Thread.sleep(2000);


        Assert.assertTrue(driver.getCurrentUrl().contains("profile"),
                "Did not navigate to Personal Information page");

        // List of important elements to validate
        By[] elementsToCheck = {
                By.id("dispay_username"),
                By.id("individual_account_type_value"),
                By.id("contact_info_display"),
                By.id("phone_number_contact_info_label"),
                By.id("individualAddressView_address_display_content"),
                By.id("individual_username_edit_button"),
                By.id("individual_email_address_edit_button"),
                By.id("individual_phone_edit_button")
        };

        // Loop through and validate all
        for (By locator : elementsToCheck) {
            WebElement element = driver.findElement(locator);
            Assert.assertTrue(element.isDisplayed(),
                    "Element not displayed: " + locator);
        }

        System.out.println("Main elements of the personal info are being displayed");
    }


    // Verifies that the user can edit personal information fields and save changes
    @Test(priority = 5)
    public void testEditPersonalInfoDetails() throws InterruptedException {

        Thread.sleep(1000);

        // Clicks the 'Edit' button for the personal address section
        WebElement editButton = driver.findElement(By.id("individual_personal_info_address_edit_button"));
        editButton.click();
        Thread.sleep(2000);

        // Locate all editable input fields in the form
        WebElement firstName = driver.findElement(By.id("firstName"));
        WebElement middleName = driver.findElement(By.id("middleName"));
        WebElement lastName = driver.findElement(By.id("lastName"));
        WebElement addressLine1 = driver.findElement(By.id("addressLine1"));
        WebElement city = driver.findElement(By.id("city"));
        WebElement postalCode = driver.findElement(By.id("postalCode"));

        // Clears and enters a new first name
        firstName.clear();
        firstName.sendKeys("Gus");

        // Clears and enters a middle initial
        middleName.clear();
        middleName.sendKeys("A");

        // Clears and enters a new last name
        lastName.clear();
        lastName.sendKeys("Fring");

        // Clears and updates the street address
        addressLine1.clear();
        addressLine1.sendKeys("10501 FGCU Blvd. S");

        // Clears and updates the city field
        city.clear();
        city.sendKeys("Fort Myers");


        // Clears and updates the zip code
        postalCode.clear();
        postalCode.sendKeys("33965");

        Thread.sleep(2000);


        // Verifies that updated values are reflected on the page
        String pageSource = driver.getPageSource();

        Assert.assertTrue(pageSource.contains("Gus"), "First name was not updated");
        Assert.assertTrue(pageSource.contains("Fring"), "Last name was not updated");
        Assert.assertTrue(pageSource.contains("10501 FGCU"), "Address was not updated");
        Assert.assertTrue(pageSource.contains("33965"), "Zip code was not updated");

        // Clicks the 'Cancel' button to discard changes
        WebElement cancelButton = driver.findElement(By.id("address_edit_cancel_button"));
        cancelButton.click();

//        // Clicks the 'Save' button to submit updated personal information
//        WebElement saveButton = driver.findElement(By.id("address_edit_submit_button"));
//        saveButton.click();


        System.out.println("Personal info is able to be edited ");
    }


}