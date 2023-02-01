package com.abhiek.ezrecipes.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            MainLayout(widthSizeClass)
        }

        // Handle app link when the app starts
        handleRecipeLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle app link while the app is running
        handleRecipeLink(intent)
    }

    private fun handleRecipeLink(appLinkIntent: Intent) {
        // Check if the intent is an app link with a URI of /recipe/RECIPE_ID
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data

        if (appLinkAction == Intent.ACTION_VIEW) {
            appLinkData?.lastPathSegment?.also { recipeId ->
                // The navigation graph will take care of the deep link ;)
                println("recipe ID = $recipeId")
            }
        }
    }
}
