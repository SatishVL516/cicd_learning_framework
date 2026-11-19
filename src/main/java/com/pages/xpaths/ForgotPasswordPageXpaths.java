package com.pages.xpaths;

import org.openqa.selenium.By;

public class ForgotPasswordPageXpaths {
    /* Forgot Password Page */
    public static final By FORGOT_PWD_TITLE_PATH = By.xpath("//span[@id='showText' and text()='Forgot Password']");
    public static final By FORGOT_PWD_SUBTITLE_PATH = By.xpath("//div[@id='kc-content-wrapper' and contains(normalize-space(.), \"Please enter the email address or username you'd like your password reset information sent to\")]");
    public static final By RECAPTCHA_IFRAME = By.cssSelector("iframe[title='reCAPTCHA']");
    public static final By RECAPTCHA_CHECKBOX = By.id("recaptcha-anchor");
    public static final By RECAPTCHA_TEXT = By.xpath("//label[@id='recaptcha-anchor-label' and text()=\"I'm not a robot\"]");
    public static final By REQ_RST_LINK_BTN_PATH = By.id("submitButton");
    public static final By BACK_TO_LOGIN_LINK_PATH = By.xpath("//a[text()='Back to Login']");
}
