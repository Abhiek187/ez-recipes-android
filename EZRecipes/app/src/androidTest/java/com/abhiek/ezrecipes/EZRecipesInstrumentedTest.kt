package com.abhiek.ezrecipes

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SmallTest
import com.abhiek.ezrecipes.ui.MainActivity
import com.abhiek.ezrecipes.ui.MainLayout
import com.abhiek.ezrecipes.ui.navbar.DrawerItem
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// JUnit 5 isn't currently supported for instrumented tests
@RunWith(AndroidJUnit4::class)
internal class EZRecipesInstrumentedTest {
    // Android Compose Rules allow access to the activity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var activity: MainActivity

    private fun printTree() {
        // Print the Semantics tree
        composeTestRule
            .onRoot(useUnmergedTree = true)
            .printToLog("Tree")
    }

    @Before
    fun init() {
        activity = composeTestRule.activity

        activity.setContent {
            MainLayout()
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

        // The navigation drawer should show the app logo and home button
        hamburgerMenu.performClick()
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.app_logo_alt))
            .assertExists()
        val homeDrawerButton = composeTestRule
            .onNodeWithText(DrawerItem.Home.title)
        homeDrawerButton.assertHasClickAction()

        // Clicking the home navigation item should show the same home page
        homeDrawerButton.performClick()
        findRecipeButton.assertExists()

        // Pressing the back button should exit out of the app
        // Don't throw an exception when closing the app
        Espresso.pressBackUnconditionally()
        assertTrue(activity.isFinishing)
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

        // Check that the share button is clickable
        val shareButton = composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.share_alt))
        shareButton.assertHasClickAction()

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
            .assertExists()
        // "Ingredients" appear in each step card
        composeTestRule
            .onAllNodesWithText(activity.getString(R.string.ingredients))
            .onFirst()
            .assertExists()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.steps))
            .assertExists()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.attribution))
            .assertExists()

        // Check that clicking the show another recipe button disables the button
        showRecipeButton.performClick()
        showRecipeButton.assertIsNotEnabled()
        composeTestRule.waitForIdle()

        // Check that clicking the home button in the hamburger menu goes to the home screen
        composeTestRule
            .onNodeWithText(DrawerItem.Home.title)
            .performClick()
        findRecipeButton.assertExists()
    }
}
