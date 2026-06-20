package com.abhiek.ezrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.NavKey
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.profile.ProfileViewModelFactory
import com.abhiek.ezrecipes.ui.util.LocalNavigationState
import com.abhiek.ezrecipes.ui.util.rememberNavigationState
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.LocalNavigator
import com.abhiek.ezrecipes.utils.Navigator
import com.abhiek.ezrecipes.utils.Routes

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val profileViewModel: ProfileViewModel by viewModels(
        factoryProducer = { ProfileViewModelFactory(this) }
    )

    private var startRoute: NavKey = Routes.Home

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val navigationState = rememberNavigationState(startRoute)
            val navigator = remember { Navigator(navigationState) }

            // Share state with child composables without prop drilling,
            // similar to EnvironmentObjects or Context Providers
            CompositionLocalProvider(
                LocalNavigationState provides navigationState,
                LocalNavigator provides navigator
            ) {
                val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                MainLayout(widthSizeClass)
            }
        }

        // Handle app link when the app starts
        handleRecipeLink(intent)
    }

    // Requires android:launchMode="singleTop" to keep the same activity running
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle app link while the app is running
        handleRecipeLink(intent)
    }

    private fun handleRecipeLink(appLinkIntent: Intent) {
        // Check the app link URI from the intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data
        if (appLinkAction != Intent.ACTION_VIEW) return

        /* The navigation graph used to take care of the deep link ;)
         * But in Navigation 3, we need to parse the deep link ourselves :'(
         * Check all the path patterns in AndroidManifest.xml
         */
        Log.d(TAG, "Received deep link: $appLinkData")

        if (appLinkData?.path?.contains("/recipe/\\d+".toRegex()) == true) {
            val recipeIdString = appLinkData.lastPathSegment
            val recipeId = recipeIdString?.toIntOrNull()

            if (recipeId == null) {
                Log.w(TAG, "Invalid recipe ID: $recipeIdString")
            } else {
                Log.d(TAG, "recipe ID = $recipeId")
                startRoute = Routes.Recipe(recipeId)
            }
        } else if (appLinkData?.path?.contains("/profile") == true) {
            val action = appLinkData.getQueryParameter("action")

            Log.d(TAG, "action = $action")
            startRoute = Routes.Profile(action)
        } else if (appLinkData?.scheme == Constants.REDIRECT_URI.scheme &&
            appLinkData?.host == Constants.REDIRECT_URI.host &&
            appLinkData?.path == Constants.REDIRECT_URI.path) {
            val code = appLinkData?.getQueryParameter("code")
            val state = appLinkData?.getQueryParameter("state")

            Log.d(TAG, "code = $code, state = $state")
            profileViewModel.oAuthResponse = Pair(code, state)
            startRoute = Routes.Profile()
        }
    }
}
