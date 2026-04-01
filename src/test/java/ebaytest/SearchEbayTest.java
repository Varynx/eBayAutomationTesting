package ebaytest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchEbayTest {
    WebDriver chromeDriver;
    JavascriptExecutor js;
    Actions actions;

    @BeforeClass
    public void start() {

        chromeDriver = new ChromeDriver();
        js = (JavascriptExecutor) chromeDriver;
        actions = new Actions(chromeDriver);
    }


//1. First Method Title
    @Test(priority = 1)
    public void firstMethod(){
        //

    }

//1. Second Method Title
    @Test(priority = 1)
    public void secondMethod(){
        //

    }



}
