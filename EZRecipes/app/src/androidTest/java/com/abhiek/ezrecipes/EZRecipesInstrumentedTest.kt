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
import com.abhiek.ezrecipes.ui.navbar.DrawerItem
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
        if (extras.getString("ci") != "true") {
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

        if (extras.getString("ci") != "true") {
            // Restore the status bar
            CleanStatusBar.disable()
        }
    }

    @SmallTest
    @Test
    fun homeScreenNavigation() {
        // Check that the hamburger menu and back button on the home screen work as expected
        // Check that the find recipe button is present
        val findRecipeButton = composeTestRule
            .onNodeWithText(activity.getString(R.string.find_recipe_button))
        findRecipeButton.assertExists()

        // The title and menu icons should be present on the top bar, but not the action buttons
        composeTestRule
            .onNodeWithText(activity.getString(R.string.app_name))
            .assertExists()
        val hamburgerMenu = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.hamburger_menu_alt))
        hamburgerMenu.assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.favorite_alt))
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.share_alt))
            .assertDoesNotExist()
        // Take screenshots along the way
        Screengrab.screenshot("home-screen-1")

        // The navigation drawer should show the app logo and home button
        hamburgerMenu.performClick()
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.app_logo_alt))
            .assertExists()
        Screengrab.screenshot("home-screen-2")
        val homeDrawerButton = composeTestRule
            .onNodeWithText(DrawerItem.Home.title)
        homeDrawerButton.assertHasClickAction()

        // Clicking the home navigation item should show the same home page
        homeDrawerButton.performClick()
        findRecipeButton.assertExists()
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

        // After clicking the find recipe button, it should be disabled
        findRecipeButton.performClick()
        findRecipeButton.assertIsNotEnabled()

        // Wait up to 30 seconds for the recipe to load
        // waitUntil defaults to 1 second before timeout
        composeTestRule.waitUntil(timeoutMillis = 30_000) {
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

        favoriteButton.performClick()
        favoriteButton.assertDoesNotExist()
        unFavoriteButton.assertExists()

        unFavoriteButton.performClick()
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

        var shotNum = 1
        Screengrab.screenshot("recipe-screen-$shotNum")
        shotNum += 1

        // Since the recipe loaded will be random, check all the elements that are guaranteed
        // to be there for all recipes
        // Check that the recipe source link is clickable
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.recipe_link))
            .assertHasClickAction()

        // Check that the two recipe buttons are clickable
        val madeButton = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.made_button))
        madeButton.assertHasClickAction()
        val showRecipeButton = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.show_recipe_button))
        showRecipeButton.assertHasClickAction()

        // Check that the nutrition label contains all the required nutritional properties
        for (label in listOf(
            activity.getString(R.string.nutrition_facts),
            activity.getString(R.string.calories),
            activity.getString(R.string.fat),
            activity.getString(R.string.saturated_fat),
            activity.getString(R.string.carbohydrates),
            activity.getString(R.string.fiber),
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
        Screengrab.screenshot("recipe-screen-${shotNum}")
        shotNum += 1

        // "Ingredients" appear in each step card
        composeTestRule
            .onAllNodesWithText(activity.getString(R.string.ingredients))
            .onFirst()
            .performScrollTo()
            .assertExists()
        Screengrab.screenshot("recipe-screen-${shotNum}")
        shotNum += 1

        composeTestRule
            .onNodeWithText(activity.getString(R.string.steps))
            .performScrollTo()
            .assertExists()
        Screengrab.screenshot("recipe-screen-${shotNum}")
        shotNum += 1

        composeTestRule
            .onNodeWithText(activity.getString(R.string.attribution))
            .performScrollTo()
            .assertExists()
        Screengrab.screenshot("recipe-screen-${shotNum}")
        shotNum += 1

        // Check that clicking the show another recipe button disables the button
        showRecipeButton
            .performScrollTo()
            .performClick()
        showRecipeButton.assertIsNotEnabled()
        composeTestRule.waitForIdle()

        // Check that clicking the home button in the hamburger menu goes to the home screen
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.hamburger_menu_alt))
            .performClick()

        composeTestRule
            .onNodeWithText(DrawerItem.Home.title)
            .performClick()
        findRecipeButton.assertExists()
    }
}
