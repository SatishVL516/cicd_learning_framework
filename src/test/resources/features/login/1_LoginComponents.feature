Feature: Login Page Components Validations

  Scenario: Validate all components on the Login page
    Given the user navigates to the Login page
    Then the left side banner image should be displayed
    And the right side application logo is displayed
    And the welcome title should be displayed
    And the login subtitle should be displayed
    And the Username label with mandatory indicator should be displayed
    And the Username input field with placeholder should be displayed
    And the Password label with mandatory indicator should be displayed
    And the Password input field with placeholder should be displayed
    And the Password visibility toggle icon should be displayed
    And the Forgot Password link should be displayed
    And the Sign In button should be displayed

  Scenario: Validate all components on the Forgot Password page
    Given the user navigates to the Login page
    When the user clicks on forgot password link
    Then the user is redirected to the forgot password page
    Then the left side banner image should be displayed
    And the right side application logo is displayed
    And the forgot password title should be displayed
    And the forgot password subtitle should be displayed
    And the Username label should be displayed
    And the Username input field with placeholder should be displayed
    And the reCAPTCHA Section should be displayed
    And the Request Reset Link button in disabled state should be displayed
    And the Back to Login link should be displayed
    When the user clicks on back to login link
    Then the user navigates to the Login page