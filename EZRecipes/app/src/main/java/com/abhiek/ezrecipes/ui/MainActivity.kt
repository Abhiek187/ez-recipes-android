package com.abhiek.ezrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
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
        // Check the app link URI from the intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data

        if (appLinkAction == Intent.ACTION_VIEW) {
            val recipeId = appLinkData?.lastPathSegment
            // The navigation graph will take care of the deep link ;)
            Log.d(TAG, "recipe ID = $recipeId")

            val action = appLinkData?.getQueryParameter("action")
            Log.d(TAG, "action = $action")
        }
    }
}
