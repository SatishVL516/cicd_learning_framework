package stepdefinitions.login;

import com.driver.WebdriverIntializer;
import com.pages.login.LoginComponentsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginComponentsSteps {

    LoginComponentsPage loginComponentsPage = new LoginComponentsPage(WebdriverIntializer.getDriver());

    @Given("the user navigates to the Login page")
    public void the_user_navigates_to_the_login_page() {
        loginComponentsPage.validateUserIsOnLoginPage();
    }

    @Then("the left side banner image should be displayed")
    public void the_left_side_banner_image_should_be_displayed() {
        loginComponentsPage.validateLeftBanner();
    }

    @Then("the right side application logo is displayed")
    public void the_right_side_application_logo_is_displayed() {
        loginComponentsPage.validateLogo();
    }

    @Then("the welcome title should be displayed")
    public void the_welcome_title_should_be_displayed() {
        loginComponentsPage.validateLoginTitle();
    }

    @Then("the login subtitle should be displayed")
    public void the_login_subtitle_should_be_displayed() {
        loginComponentsPage.validateLoginSubtitle();
    }

    @Then("the Username label with mandatory indicator should be displayed")
    public void the_username_label_with_mandatory_indicator_should_be_displayed() {
        loginComponentsPage.validateUsernameLabelWithMandatoryStar();
    }

    @Then("the Username input field with placeholder should be displayed")
    public void the_username_input_field_with_placeholder_should_be_displayed() {
        loginComponentsPage.validateUsernameInputField();
    }

    @Then("the Password label with mandatory indicator should be displayed")
    public void the_password_label_with_mandatory_indicator_should_be_displayed() {
        loginComponentsPage.validatePasswordLabelWithMandatoryStar();
    }

    @Then("the Password input field with placeholder should be displayed")
    public void the_password_input_field_with_placeholder_should_be_displayed() {
        loginComponentsPage.validatePasswordInputField();
    }

    @Then("the Password visibility toggle icon should be displayed")
    public void the_password_visibility_toggle_icon_should_be_displayed() {
        loginComponentsPage.validatePasswordEyeIcon();
    }

    @Then("the Forgot Password link should be displayed")
    public void the_forgot_password_link_should_be_displayed() {
        loginComponentsPage.validateForgotPasswordLink();
    }

    @Then("the Sign In button should be displayed")
    public void the_sign_in_button_should_be_displayed() {
        loginComponentsPage.validateSignInButton();
    }

    @When("the user clicks on forgot password link")
    public void the_user_clicks_on_forgot_password_link() {
        loginComponentsPage.clickOnForgotPasswordLink();
    }

    @Then("the user is redirected to the forgot password page")
    public void the_user_is_redirected_to_the_forgot_password_page() {
        loginComponentsPage.validateUserIsOnForgotPasswordPage();
    }

    @Then("the forgot password title should be displayed")
    public void the_forgot_password_title_should_be_displayed() {
        loginComponentsPage.validateForgotPasswordTitle();

    }

    @Then("the forgot password subtitle should be displayed")
    public void the_forgot_password_subtitle_should_be_displayed() {
        loginComponentsPage.validateForgotPasswordSubtitle();
    }

    @Then("the Username label should be displayed")
    public void the_username_label_should_be_displayed() {
        loginComponentsPage.validateUsernameLabel();
    }

    @Then("the reCAPTCHA Section should be displayed")
    public void the_re_captcha_section_should_be_displayed() {
        loginComponentsPage.validateRecaptchaIsDisplayed();
    }

    @Then("the Request Reset Link button in disabled state should be displayed")
    public void the_request_reset_link_button_in_disabled_state_should_be_displayed() {
        loginComponentsPage.validateRequestResetLinkButton();
    }

    @Then("the Back to Login link should be displayed")
    public void the_back_to_login_link_should_be_displayed() {
        loginComponentsPage.validateBackToLoginLink();
    }

    @When("the user clicks on back to login link")
    public void the_user_clicks_on_back_to_login_link() {
        loginComponentsPage.clickOnBackToLoginLink();
    }


}
