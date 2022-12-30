package com.abhiek.ezrecipes

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @Before
    fun init() {
        activity = composeTestRule.activity

        activity.setContent {
            MainLayout()
        }
    }

    @Test
    fun homeScreenNavigation() {
        // Check that the hamburger menu and back button on the home screen work as expected
        // Check that the find recipe button is present
        composeTestRule
            .onNodeWithText(activity.getString(R.string.find_recipe_button))
            .assertExists()

        // The title and menu icons should be present on the top bar, but not the action buttons
        composeTestRule
            .onNodeWithText(activity.getString(R.string.app_name))
            .assertExists()
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.hamburger_menu_alt))
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.favorite_alt))
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.share_alt))
            .assertDoesNotExist()

        // The navigation drawer should show the app logo and home button
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.hamburger_menu_alt))
            .performClick()
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.app_logo_alt))
            .assertExists()
        composeTestRule
            .onNodeWithText(DrawerItem.Home.title)
            .assertHasClickAction()

        // Clicking the home navigation item should show the same home page
        composeTestRule
            .onNodeWithText(DrawerItem.Home.title)
            .performClick()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.find_recipe_button))
            .assertExists()

        // Pressing the back button should exit out of the app
        // Don't throw an exception when closing the app
        Espresso.pressBackUnconditionally()
        assertTrue(activity.isFinishing)
    }

    @Test
    fun findMeARecipe() {}
}
