package com.pages.login;

import com.pages.xpaths.ForgotPasswordPageXpaths;
import com.pages.xpaths.LoginPageXpaths;
import com.utils.CommonMethods;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class LoginComponentsPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private CommonMethods utils;

    public LoginComponentsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        this.utils = new CommonMethods(driver);
    }


    // 1
    public void validateUserIsOnLoginPage() {
        waitForLeftBannerImageLoad();
        wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageXpaths.LOGIN_TITLE_PATH));
    }

    // 2
    public void validateLeftBanner() {
        utils.isElementDisplayed(LoginPageXpaths.LOGIN_BANNER_PATH);
    }

    // 3
    public void validateLogo() {
        utils.isElementDisplayed(LoginPageXpaths.LOGIN_LOGO_PATH);
    }

    // 4
    public void validateLoginTitle() {
        utils.isElementDisplayed(LoginPageXpaths.LOGIN_TITLE_PATH);
    }

    // 5
    public void validateLoginSubtitle() {
        utils.isElementDisplayed(LoginPageXpaths.LOGIN_SUBTITLE_PATH);
    }

    // 6
    public void validateUsernameLabelWithMandatoryStar() {
        utils.isElementDisplayed(LoginPageXpaths.UN_LBL_PATH);
        utils.isElementDisplayed(LoginPageXpaths.UN_STAR_PATH);
    }

    // 7
    public void validateUsernameInputField() {
        utils.isElementDisplayed(LoginPageXpaths.UN_INP_PATH);
        utils.isElementDisplayed(LoginPageXpaths.UN_PLH_PATH);
    }

    // 8
    public void validatePasswordLabelWithMandatoryStar() {
        utils.isElementDisplayed(LoginPageXpaths.PWD_LBL_PATH);
        utils.isElementDisplayed(LoginPageXpaths.PWD_STAR_PATH);
    }

    // 9
    public void validatePasswordInputField() {
        utils.isElementDisplayed(LoginPageXpaths.PWD_INP_PATH);
        utils.isElementDisplayed(LoginPageXpaths.PWD_PLH_PATH);
        // Password should be in *** by default
        Assert.assertEquals(driver.findElement(LoginPageXpaths.PWD_INP_PATH).getAttribute("type"), "password");
    }

    // 10
    public void validatePasswordEyeIcon() {
        utils.isElementDisplayed(LoginPageXpaths.PWD_EYE_PATH);
        // Default state - password hidden
        validateEyeIconState(true);
        togglePasswordVisibility(false); // Show password
        togglePasswordVisibility(true);  // Hide password
    }

    // 11
    public void validateForgotPasswordLink() {
        utils.isElementDisplayed(LoginPageXpaths.FORGOT_PWD_LINK_PATH);
    }

    // 12
    public void validateSignInButton() {
        utils.isElementDisplayed(LoginPageXpaths.SIGN_IN_BTN_PATH);
    }


    // Forgot Password Page
    public void clickOnForgotPasswordLink() {
        wait.until(ExpectedConditions.elementToBeClickable(LoginPageXpaths.FORGOT_PWD_LINK_PATH)).click();
    }

    public void validateUserIsOnForgotPasswordPage() {
        waitForLeftBannerImageLoad();
        wait.until(ExpectedConditions.visibilityOfElementLocated(ForgotPasswordPageXpaths.FORGOT_PWD_TITLE_PATH));
    }

    public void validateForgotPasswordTitle() {
        utils.isElementDisplayed(ForgotPasswordPageXpaths.FORGOT_PWD_TITLE_PATH);
    }

    // 5
    public void validateForgotPasswordSubtitle() {
        utils.isElementDisplayed(ForgotPasswordPageXpaths.FORGOT_PWD_SUBTITLE_PATH);
    }

    public void validateUsernameLabel() {
        utils.isElementDisplayed(LoginPageXpaths.UN_LBL_PATH);
    }

    public void validateRecaptchaIsDisplayed() {
        utils.isElementDisplayed(ForgotPasswordPageXpaths.RECAPTCHA_IFRAME);
        driver.switchTo().frame(wait.until(ExpectedConditions.visibilityOfElementLocated(ForgotPasswordPageXpaths.RECAPTCHA_IFRAME)));
        utils.isElementDisplayed(ForgotPasswordPageXpaths.RECAPTCHA_CHECKBOX);
        Assert.assertEquals(driver.findElement(ForgotPasswordPageXpaths.RECAPTCHA_TEXT).getText(), "I'm not a robot");
        driver.switchTo().defaultContent();
    }

    public void validateRequestResetLinkButton() {
        utils.isElementDisplayed(ForgotPasswordPageXpaths.REQ_RST_LINK_BTN_PATH);
        Assert.assertTrue(!driver.findElement(ForgotPasswordPageXpaths.REQ_RST_LINK_BTN_PATH).isEnabled(), "Request Reset Link button should be disabled.");
    }

    public void validateBackToLoginLink() {
        utils.isElementDisplayed(ForgotPasswordPageXpaths.BACK_TO_LOGIN_LINK_PATH);
    }

    public void clickOnBackToLoginLink() {
        wait.until(ExpectedConditions.elementToBeClickable(ForgotPasswordPageXpaths.BACK_TO_LOGIN_LINK_PATH)).click();
    }

    // Sub Methods
    public void waitForLeftBannerImageLoad() {
        // Wait Until the Banner Loads on the Left Side Completely
        WebElement banner = driver.findElement(LoginPageXpaths.LOGIN_BANNER_PATH);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver -> (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].complete && arguments[0].naturalWidth > 0;", banner));
    }

    private void togglePasswordVisibility(boolean hidePassword) {
        driver.findElement(LoginPageXpaths.PWD_EYE_PATH).click();
        wait.until(ExpectedConditions.attributeToBe(LoginPageXpaths.PWD_INP_PATH, "type", hidePassword ? "password" : "text"));
        validateEyeIconState(hidePassword);
    }

    private void validateEyeIconState(boolean isPasswordHidden) {
        String hiddenEyeStyle = driver.findElement(LoginPageXpaths.PWD_EYE_SLASH_PATH).getCssValue("display");
        String visibleEyeStyle = driver.findElement(LoginPageXpaths.PWD_EYE_FILL_PATH).getCssValue("display");
        Assert.assertEquals(hiddenEyeStyle.equals("none"), isPasswordHidden ? false : true, "Closed eye icon visibility is incorrect.");
        Assert.assertEquals(visibleEyeStyle.equals("none"), isPasswordHidden ? true : false, "Open eye icon visibility is incorrect.");
    }
}
