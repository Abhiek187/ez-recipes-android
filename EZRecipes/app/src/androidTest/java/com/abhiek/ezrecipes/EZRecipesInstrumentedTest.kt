package com.abhiek.ezrecipes

import android.app.Activity
import android.app.Instrumentation
import android.content.ClipDescription
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.abhiek.ezrecipes.ui.MainActivity
import com.abhiek.ezrecipes.ui.MainLayout
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.cleanstatusbar.CleanStatusBar

// JUnit 5 isn't currently supported for instrumented tests
@RunWith(AndroidJUnit4::class)
internal class EZRecipesInstrumentedTest {
    // Android Compose Rules allow access to the activity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var activity: MainActivity
    private val extras = InstrumentationRegistry.getArguments()
    private val isLocal = extras.getString("ci") != "true"

    private fun screenshot(name: String, shotNum: Int? = null) {
        if (isLocal) {
            var screenshotName = name
            if (shotNum != null) screenshotName += "-$shotNum"
            Screengrab.screenshot(screenshotName)
        }
    }

    private fun printTree() {
        // Print the Semantics tree
        composeTestRule
            .onRoot(useUnmergedTree = true)
            .printToLog("Tree")
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Before
    fun setUp() {
        activity = composeTestRule.activity
        // Start tracking intents before each test
        Intents.init()

        // CleanStatusBar times out on GitHub Actions
        // It's only possible to pass strings as arguments
        if (isLocal) {
            // Clear the status bar when taking screenshots
            CleanStatusBar.enableWithDefaults()
        }

        activity.setContent {
            val widthSizeClass = calculateWindowSizeClass(activity).widthSizeClass
            MainLayout(widthSizeClass)
        }
    }

    @After
    fun tearDown() {
        // Clear intents state after each test
        Intents.release()

        if (isLocal) {
            // Restore the status bar
            CleanStatusBar.disable()
        }
    }

    @LargeTest
    @Test
    fun findMeARecipe() {
        // Click the find recipe button and check that the recipe page renders properly
        // Large test since it makes a network request and uses quota
        // When first launching the app, the find recipe button should be clickable
        val findRecipeButton = composeTestRule
            .onNodeWithText(activity.getString(R.string.find_recipe_button))
        findRecipeButton.assertIsEnabled()

        // The title and menu icons should be present on the top bar, but not the action buttons
        composeTestRule
            .onNodeWithText(activity.getString(R.string.app_name))
            .assertExists()
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.share_alt))
            .assertDoesNotExist()
        // Take screenshots along the way
        var shotNum = 1
        screenshot("home-screen", shotNum)
        shotNum += 1

        // The accordions should be present, but not display any recipes
        val favoriteAccordion = composeTestRule
            .onNodeWithText(activity.getString(R.string.profile_favorites))
        val recentAccordion = composeTestRule
            .onNodeWithText(activity.getString(R.string.profile_recently_viewed))
        val ratingAccordion = composeTestRule
            .onNodeWithText(activity.getString(R.string.profile_ratings))
        val signInMessage = composeTestRule
            .onNodeWithText(activity.getString(R.string.sign_in_for_recipes))
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.accordion_expand))
            .assertCountEquals(3)
        composeTestRule
            .onAllNodesWithContentDescription(activity.getString(R.string.accordion_collapse))
            .assertCountEquals(0)

        favoriteAccordion.performClick()
        signInMessage.assertExists()
        favoriteAccordion.performClick()
        recentAccordion.performClick()
        signInMessage.assertDoesNotExist()
        recentAccordion.performClick()
        ratingAccordion.performClick()
        signInMessage.assertExists()
        ratingAccordion.performClick()

        // After clicking the find recipe button, it should be disabled
        findRecipeButton.performClick()
        findRecipeButton.assertIsNotEnabled()

        // Wait up to 30 seconds for the recipe to load
        // waitUntil defaults to 1 second before timeout
        composeTestRule.waitUntil(timeoutMillis = 30_000) {
            // Expression must be a boolean
            composeTestRule
                .onAllNodesWithText(activity.getString(R.string.find_recipe_button))
                .fetchSemanticsNodes()
                .isEmpty()
        }

        // Check that the favorite button toggles between filling and un-filling when tapped
        val favoriteButton = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.favorite_alt))
        val unFavoriteButton = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.un_favorite_alt))
        favoriteButton.assertExists()
        unFavoriteButton.assertDoesNotExist()

        // Check that the share button opens the Sharesheet
        val shareButton = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.share_alt))
        shareButton.assertHasClickAction()

        // Mock the Sharesheet intent (based on https://stackoverflow.com/a/60629289)
        val sendIntent = allOf(
            hasAction(Intent.ACTION_SEND),
            hasExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.share_body)),
            hasType(ClipDescription.MIMETYPE_TEXT_PLAIN)
        )
        val shareIntent = allOf(
            hasAction(Intent.ACTION_CHOOSER),
            hasExtra(Intent.EXTRA_INTENT, sendIntent)
        )
        // intending = mock intent result, intended = check if intent was performed
        intending(shareIntent).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        )
        shareButton.performClick()
        intended(shareIntent)

        val screenshotName = "recipe-screen"
        shotNum = 1
        screenshot(screenshotName, shotNum)
        shotNum += 1

        // Since the recipe loaded will be random, check all the elements that are guaranteed
        // to be there for all recipes
        // Check that the recipe source link is clickable
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.recipe_link))
            .assertHasClickAction()

        // Check that the recipe button is clickable
        val showRecipeButton = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.show_recipe_button))
        showRecipeButton.assertHasClickAction()

        // Check that the nutrition label contains all the required nutritional properties
        // (except fiber)
        for (label in listOf(
            activity.getString(R.string.nutrition_facts),
            activity.getString(R.string.calories),
            activity.getString(R.string.fat),
            activity.getString(R.string.saturated_fat),
            activity.getString(R.string.carbohydrates),
            activity.getString(R.string.sugar),
            activity.getString(R.string.protein),
            activity.getString(R.string.cholesterol),
            activity.getString(R.string.sodium)
        )) {
            /* More than one match may exist in places like the ingredients list,
             * but the first match should be in the nutrition label since these matchers
             * match full strings in EditTexts and TextViews and are case sensitive
             */
            composeTestRule
                .onAllNodesWithText(label)
                .onFirst()
                .assertExists()
        }

        // Check that the summary box, ingredients list, instructions list, and footer are present
        composeTestRule
            .onNodeWithText(activity.getString(R.string.summary))
            .performScrollTo()
            .assertExists()
        screenshot(screenshotName, shotNum)
        shotNum += 1

        // "Ingredients" appear in each step card
        composeTestRule
            .onAllNodesWithText(activity.getString(R.string.ingredients))
            .onFirst()
            .performScrollTo()
            .assertExists()
        screenshot(screenshotName, shotNum)
        shotNum += 1

        composeTestRule
            .onNodeWithText(activity.getString(R.string.steps))
            .performScrollTo()
            .assertExists()
        screenshot(screenshotName, shotNum)
        shotNum += 1

        composeTestRule
            .onNodeWithText(activity.getString(R.string.attribution))
            .performScrollTo()
            .assertExists()
        screenshot(screenshotName, shotNum)
        shotNum += 1

        // Check that clicking the show another recipe button disables the button
        showRecipeButton
            .performScrollTo()
            .performClick()
        showRecipeButton.assertIsNotEnabled()
        composeTestRule.waitForIdle()

        // Check that pressing the back button goes to the home screen
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        findRecipeButton.assertExists()
    }

    @LargeTest
    @Test
    fun searchRecipes() {
        // Click the search tab
        // Use the hamburger menu on large screens
        val hamburgerMenu = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.hamburger_menu_alt))
        val screenshotName = "search-screen"
        var shotNum = 1

        if (hamburgerMenu.isDisplayed()) {
            hamburgerMenu
                .assertHasClickAction()
                .performClick()
            composeTestRule
                .onNodeWithContentDescription(activity.getString(R.string.app_logo_alt))
                .assertExists()
            screenshot(screenshotName, shotNum)
            shotNum += 1
        }

        val searchTab = composeTestRule
            .onNodeWithText(activity.getString(R.string.search_tab))
        searchTab.performClick()

        // Interact with all the filter options
        // The results placeholder should only show on large screens
        val resultsTitle = composeTestRule
            .onNodeWithText(activity.getString(R.string.results_title))
        val resultsPlaceholder = composeTestRule
            .onNodeWithText(activity.getString(R.string.results_placeholder))

        if (resultsPlaceholder.isDisplayed()) {
            resultsTitle.assertExists()
            resultsPlaceholder.assertExists()
        }

        screenshot(screenshotName, shotNum)
        shotNum += 1
        composeTestRule
            .onNodeWithText(activity.getString(R.string.query_section))
            .performTextInput("pasta")

        composeTestRule
            .onNodeWithText(activity.getString(R.string.calorie_unit))
            .assertExists()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.min_cals_placeholder))
            .performTextInput("500")

        // Check that all the form errors appear
        val maxCalTextField = composeTestRule
            .onNodeWithText(activity.getString(R.string.max_cals_placeholder))
        maxCalTextField.performTextInput("80")
        val calorieRangeError = composeTestRule
            .onNodeWithText(activity.getString(R.string.calorie_invalid_range_error))
        calorieRangeError.assertExists()
        val submitButton = composeTestRule
            .onNodeWithText(activity.getString(R.string.apply_button))
        submitButton.assertIsNotEnabled()

        composeTestRule
            .onNodeWithText("80")
            .performTextInput("00")
        val maxCaloriesError = composeTestRule
            .onNodeWithText(activity.getString(R.string.calorie_exceed_max_error))
        maxCaloriesError.assertExists()
        submitButton.assertIsNotEnabled()

        composeTestRule
            .onNodeWithText("8000")
            .performTextClearance()
        maxCalTextField.performTextInput("800")
        calorieRangeError.assertDoesNotExist()
        maxCaloriesError.assertDoesNotExist()
        submitButton.assertIsEnabled()

        composeTestRule
            .onNodeWithText(activity.getString(R.string.vegetarian_label))
            .assertExists()
            .performClick()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.vegan_label))
            .assertExists()
            .performClick()
            .performClick()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.gluten_free_label))
            .assertExists()
            .performClick()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.healthy_label))
            .assertExists()
            .performClick()
            .performClick()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.cheap_label))
            .assertExists()
            .performClick()
            .performClick()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.sustainable_label))
            .assertExists()
            .performClick()
            .performClick()

        val ratingDropdown = composeTestRule
            .onNodeWithText(activity.getString(R.string.rating_label))
        ratingDropdown
            .performScrollTo()
            .performClick()
        for (rating in 1..5) {
            composeTestRule
                .onNodeWithText(rating.toString())
                .performClick()
        }
        composeTestRule
            .onNodeWithText(activity.getString(R.string.option_none))
            .performClick()
        ratingDropdown.performClick()

        val spiceLevelDropdown = composeTestRule
            .onNodeWithText(activity.getString(R.string.spice_label))
        spiceLevelDropdown
            .performScrollTo()
            .performClick()
        composeTestRule
            .onNodeWithText("none")
            .performClick()
        composeTestRule
            .onNodeWithText("mild")
            .performClick()
        composeTestRule
            .onNodeWithText("spicy")
            .performClick()
            .performClick()
        spiceLevelDropdown.performClick()

        val mealTypeDropdown = composeTestRule
            .onNodeWithText(activity.getString(R.string.type_label))
        mealTypeDropdown
            .performScrollTo()
            .performClick()
        composeTestRule
            .onNodeWithText("dinner")
            .performClick()
        composeTestRule
            .onNodeWithText("lunch")
            .performScrollTo()
            .performClick()
        composeTestRule
            .onNodeWithText("main course")
            .performScrollTo()
            .performClick()
        composeTestRule
            .onNodeWithText("main dish")
            .performScrollTo()
            .performClick()
        mealTypeDropdown.performClick()

        val cuisineDropdown = composeTestRule
            .onNodeWithText(activity.getString(R.string.culture_label))
        cuisineDropdown
            .performScrollTo()
            .performClick()
        composeTestRule
            .onNodeWithText("Italian")
            .performScrollTo()
            .performClick()
        cuisineDropdown.performClick()
        screenshot(screenshotName, shotNum)
        shotNum += 1

        // Submit the form and wait for results
        submitButton
            .performScrollTo()
            .performClick()

        if (resultsPlaceholder.isDisplayed()) {
            // Wait until the placeholder disappears on large screens
            composeTestRule.waitUntil(timeoutMillis = 30_000) {
                resultsPlaceholder.isNotDisplayed()
            }
        } else {
            // Wait until the results are shown on small screens
            composeTestRule.waitUntil(timeoutMillis = 30_000) {
                resultsTitle.isDisplayed()
            }
        }
        screenshot(screenshotName, shotNum)
        shotNum += 1
    }

    @SmallTest
    @Test
    fun glossaryScreen() {
        // Take a screenshot of the glossary tab (no assertions)
        val hamburgerMenu = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.hamburger_menu_alt))

        if (hamburgerMenu.isDisplayed()) {
            hamburgerMenu.performClick()
        }

        val glossaryTab = composeTestRule
            .onNodeWithText(activity.getString(R.string.glossary_tab))
        glossaryTab.performClick()

        // Wait until the glossary screen is visible before taking a screenshot
        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithText(activity.getString(R.string.find_recipe_button))
                .fetchSemanticsNodes()
                .isEmpty()
        }
        screenshot("glossary-screen")
    }

    @LargeTest
    @Test
    fun profileScreen() {
        val hamburgerMenu = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.hamburger_menu_alt))

        if (hamburgerMenu.isDisplayed()) {
            hamburgerMenu.performClick()
        }

        val profileTab = composeTestRule
            .onNodeWithText(activity.getString(R.string.profile_tab))
        profileTab.performClick()

        // Wait until the profile loads (should be logged out)
        composeTestRule.waitUntil(timeoutMillis = 30_000) {
            composeTestRule
                .onNodeWithText(activity.getString(R.string.profile_loading))
                .isNotDisplayed()
        }

        val screenshotName = "profile-screen"
        var shotNum = 1
        screenshot(screenshotName, shotNum)
        shotNum += 1
        composeTestRule
            .onNodeWithText(activity.getString(R.string.login_message))
            .assertExists()

        val loginButton = composeTestRule
            .onNodeWithText(activity.getString(R.string.login))
        loginButton
            .assertExists()
            .performClick()

        // Check all the validations on the login, create account, & forget password forms
        val profileTest = ProfileTest(
            composeTestRule, activity, ::screenshot, screenshotName, shotNum
        )
        profileTest.testSignIn()
        profileTest.testSignUp()
        profileTest.testForgetPassword()
    }
}
