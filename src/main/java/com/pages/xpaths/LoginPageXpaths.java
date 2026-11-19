package com.pages.xpaths;

import org.openqa.selenium.By;

public class LoginPageXpaths {
    /* Login Page */
    public static final By LOGIN_BANNER_PATH = By.xpath("//div[@class='left-content']/img[@alt='backgound']");
    public static final By LOGIN_LOGO_PATH = By.xpath("//div[@class='logo-container']/img[@alt='Logo']");
    public static final By LOGIN_TITLE_PATH = By.xpath("//span[@id='showText' and text()='Welcome to Kavach']");
    public static final By LOGIN_SUBTITLE_PATH = By.xpath("//span[@id='sign-in' and text()='Login to access your account']");
    public static final By UN_LBL_PATH = By.xpath("//label[@for='username' and text()='Username or Email']");
    public static final By UN_STAR_PATH = By.xpath("//label[@for='username']/span[text()='*']");
    public static final By UN_INP_PATH = By.id("username");
    public static final By UN_PLH_PATH = By.xpath("//input[@placeholder='Username or Email']");
    public static final By PWD_LBL_PATH = By.xpath("//label[@for='password' and text()='Password']");
    public static final By PWD_STAR_PATH = By.xpath("//label[@for='password']/span[text()='*']");
    public static final By PWD_INP_PATH = By.id("password");
    public static final By PWD_PLH_PATH = By.xpath("//input[@placeholder='Password']");
    public static final By PWD_EYE_PATH = By.cssSelector("button[onclick='clickEyeButton()']");
    public static final By PWD_EYE_SLASH_PATH = By.id("eye-icon-slash");
    public static final By PWD_EYE_FILL_PATH = By.id("eye-icon-fill");
    public static final By FORGOT_PWD_LINK_PATH = By.xpath("//a[text()='Forgot Password?']");
    public static final By SIGN_IN_BTN_PATH = By.id("kc-login");
    public static final By INV_ERR_MSG_PATH = By.xpath("//div[@id='kc-content-wrapper']/small[@id='input-error']");
    public static final By UN_REQ_MSG_PATH = By.xpath("//input[@id='username']/parent::div/following-sibling::small[@id='input-error']");
    public static final By PWD_REQ_MSG_PATH = By.xpath("//input[@id='password']/parent::div/following-sibling::small[@id='input-error']");






}
