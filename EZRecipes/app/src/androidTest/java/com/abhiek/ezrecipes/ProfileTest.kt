package com.abhiek.ezrecipes

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.abhiek.ezrecipes.ui.MainActivity

internal class ProfileTest(
    private val composeTestRule: AndroidComposeTestRule<
            ActivityScenarioRule<MainActivity>, MainActivity>,
    private val activity: MainActivity,
    private val screenshot: (String, Int?) -> Unit,
    private val screenshotName: String,
    private var shotNum: Int
) {
    private val signInNode = composeTestRule
        .onNodeWithText(activity.getString(R.string.sign_in_header))
    private val emailField = composeTestRule
        .onNodeWithText(activity.getString(R.string.email_field))
    private val emailRequiredError = composeTestRule
        .onNodeWithText(activity.getString(R.string.field_required, "Email"))
    private val emailInvalidError = composeTestRule
        .onNodeWithText(activity.getString(R.string.email_invalid))
    private val passwordField = composeTestRule
        .onNodeWithText(activity.getString(R.string.password_field))
    private val passwordRequiredError = composeTestRule
        .onNodeWithText(activity.getString(R.string.field_required, "Password"))

    fun testSignIn() {
        val signUpNode = composeTestRule
            .onNodeWithText(activity.getString(R.string.sign_up_header))
        signInNode
            .assertExists()
            .assertHasNoClickAction()
        signUpNode
            .assertExists()
            .assertHasClickAction()
        // Distinguish the login button in the dialog from the one on the profile screen
        val loginDialogButton = composeTestRule
            .onNodeWithTag("login_dialog_button")
        loginDialogButton.assertIsNotEnabled()
        screenshot(screenshotName, shotNum)
        shotNum += 1

        /*
         * Username Check:
         * - No error should be shown initially
         * - An error should be shown if the field is empty
         */
        val usernameField = composeTestRule
            .onNodeWithText(activity.getString(R.string.username_field))
        val usernameRequiredError = composeTestRule
            .onNodeWithText(activity.getString(R.string.field_required, "Username"))
        usernameRequiredError.assertDoesNotExist()
        usernameField.requestFocus()
        usernameRequiredError.assertExists()
        usernameField.performTextInput("test@example.com")
        loginDialogButton.assertIsNotEnabled()

        /*
         * Password Check:
         * - No error should be shown initially
         * - An error should be shown if the field is empty
         * - The eye icon should toggle the password's visibility
         */
        passwordRequiredError.assertDoesNotExist()
        passwordField.requestFocus()
        passwordRequiredError.assertExists()
        passwordField.performTextInput("password")
        // The eye icon appears twice on the sign up form
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_hide))
            .assertCountEquals(0)
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_show))
            .assertCountEquals(1)
            .onFirst()
            .performClick()
            .assertDoesNotExist()
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_hide))
            .assertCountEquals(1)
            .onFirst()
            .performClick()
            .assertDoesNotExist()
        loginDialogButton.assertIsEnabled()
        signUpNode.performClick()
    }

    fun testSignUp() {
        signInNode.assertHasClickAction()
        // There are 2 sign up texts on the create account form
        val signUpButton = composeTestRule
            .onNodeWithTag("sign_up_button")
        signUpButton.assertIsNotEnabled()
        screenshot(screenshotName, shotNum)
        shotNum += 1

        /*
         * Email check:
         * - No error should be shown initially
         * - An error should be shown if the field is empty
         * - An error should be shown if the email is invalid
         */
        emailRequiredError.assertDoesNotExist()
        emailInvalidError.assertDoesNotExist()
        emailField.requestFocus()
        emailRequiredError.assertExists()
        emailField.performTextInput("test")
        emailInvalidError.assertExists()
        emailField.performTextInput("@example.com")
        signUpButton.assertIsNotEnabled()

        /*
         * Password check:
         * - No error should be shown initially
         * - An error should be shown if the field is empty
         * - An error should be shown if the password is too short
         * - The eye icon should toggle the password's visibility
         */
        // The min length message can appear below both password fields
        passwordRequiredError.assertDoesNotExist()
        composeTestRule
            .onAllNodesWithText(activity.getString(R.string.password_min_length))
            .assertCountEquals(1)
        passwordField.requestFocus()
        passwordRequiredError.assertExists()
        passwordField.performTextInput("pass")
        composeTestRule
            .onAllNodesWithText(activity.getString(R.string.password_min_length))
            .assertCountEquals(2)
        passwordField.performTextInput("word")
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_hide))
            .assertCountEquals(0)
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_show))
            .assertCountEquals(2)
            .onFirst()
            .performClick()
            .assertDoesNotExist()
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_hide))
            .assertCountEquals(2)
            .onFirst()
            .performClick()
            .assertDoesNotExist()
        signUpButton.assertIsNotEnabled()

        /*
         * Confirm Password check:
         * - No error should be shown initially
         * - An error should be shown if the field is empty
         * - An error should be shown if the passwords don't match
         * - The eye icon should toggle the password's visibility
         */
        val confirmPasswordField = composeTestRule
            .onNodeWithText(activity.getString(R.string.password_confirm_field))
        val passwordMatchError = composeTestRule
            .onNodeWithText(activity.getString(R.string.password_match))
        composeTestRule
            .onAllNodesWithText(activity.getString(R.string.password_min_length))
            .assertCountEquals(1)
        passwordMatchError.assertDoesNotExist()
        confirmPasswordField.requestFocus()
        composeTestRule
            .onAllNodesWithText(activity.getString(R.string.password_min_length))
            .assertCountEquals(0)
        passwordMatchError.assertExists()
        confirmPasswordField.performTextInput("password")
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_hide))
            .assertCountEquals(0)
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_show))
            .assertCountEquals(2)
            .onLast()
            .performClick()
            .assertDoesNotExist()
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.password_hide))
            .assertCountEquals(2)
            .onLast()
            .performClick()
            .assertDoesNotExist()
        signUpButton.assertIsEnabled()
        signInNode.performClick()
    }

    fun testForgetPassword() {
        val passwordForgetButton = composeTestRule
            .onNodeWithText(activity.getString(R.string.password_forget))
        passwordForgetButton
            .assertExists()
            .performClick()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.forget_password_header))
            .assertExists()
        screenshot(screenshotName, shotNum)
        shotNum += 1
        val submitButton = composeTestRule
            .onNodeWithText(activity.getString(R.string.submit_button))
        submitButton.assertIsNotEnabled()

        /*
         * Email check:
         * - No error should be shown initially
         * - An error should be shown if the field is empty
         * - An error should be shown if the email is invalid
         */
        emailRequiredError.assertDoesNotExist()
        emailInvalidError.assertDoesNotExist()
        emailField.requestFocus()
        emailRequiredError.assertExists()
        emailField.performTextInput("test")
        emailInvalidError.assertExists()
        emailField.performTextInput("@example.com")
        submitButton.assertIsEnabled()
    }
}
