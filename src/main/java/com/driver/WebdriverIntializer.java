package com.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Assert;

public class WebdriverIntializer {
    public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    private static final boolean IS_HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "false"));

    /**
     * This method is used to initialize the threadlocal driver on the basis of given browser
     *
     * @param browser
     * @return this will return tldriver.
     */

    public WebDriver init_driver(String browser) {
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--window-size=1920,1080");
                if (IS_HEADLESS) {
                    options.addArguments("--headless=new");
                }
                tlDriver.set(new ChromeDriver(options));
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions f_options = new FirefoxOptions();
                f_options.addArguments("--remote-allow-origins=*");
                f_options.addArguments("window-size=1400,800");
                if (IS_HEADLESS) {
                    f_options.addArguments("--headless=new");
                }
                tlDriver.set(new FirefoxDriver(f_options));
                break;
            case "safari":
                WebDriverManager.safaridriver().setup();
                tlDriver.set(new SafariDriver());
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless");
                tlDriver.set(new EdgeDriver(edgeOptions));
                break;
            default:
                Assert.fail("Please pass the correct browser value: " + browser);
        }
        getDriver().manage().deleteAllCookies();
        return getDriver();
    }

    /**
     * this is used to get the driver with ThreadLocal
     * @return this will return tldriver.
     */
    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }
}
