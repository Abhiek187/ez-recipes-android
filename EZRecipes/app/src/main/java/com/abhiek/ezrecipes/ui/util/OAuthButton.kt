package com.abhiek.ezrecipes.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.auth.AuthTabIntent
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Provider
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants

/**
 * A simple data class to hold the raw results from the auth flow,
 * since we cannot instantiate the library's internal AuthTabIntent.AuthResult.
 */
private data class AppAuthResult(val resultCode: Int, val resultUri: Uri?)

// Based on the AuthTabIntent source code:
// https://android.googlesource.com/platform/frameworks/support/+/androidx-main/browser/browser/src/main/java/androidx/browser/auth/AuthTabIntent.java
private class AppAuthContract: ActivityResultContract<Intent, AppAuthResult>() {
    override fun createIntent(context: Context, input: Intent): Intent {
        // The contract simply passes the prepared intent through to be launched.
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): AppAuthResult {
        // Parse the raw result into our own data class.
        // The logic for which codes are valid is based on the AuthTabIntent source.
        val resultUri = if (resultCode == AuthTabIntent.RESULT_OK) intent?.data else null
        val finalResultCode = when (resultCode) {
            AuthTabIntent.RESULT_OK,
            AuthTabIntent.RESULT_CANCELED,
            AuthTabIntent.RESULT_VERIFICATION_FAILED,
            AuthTabIntent.RESULT_VERIFICATION_TIMED_OUT -> resultCode
            else -> AuthTabIntent.RESULT_UNKNOWN_CODE
        }
        return AppAuthResult(finalResultCode, resultUri)
    }
}

// Inspired by https://github.com/firebase/FirebaseUI-Android/blob/master/auth/src/main/java/com/firebase/ui/auth/ui/components/AuthProviderButton.kt
@Composable
fun OAuthButton(
    provider: Provider,
    authUrl: String? = null,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val tag = "OAuthButton"

    val launcher = rememberLauncherForActivityResult(AppAuthContract()) { result ->
        val authResult = when (result.resultCode) {
            AuthTabIntent.RESULT_OK -> "Received auth result, Uri: ${result.resultUri}"
            AuthTabIntent.RESULT_CANCELED -> "AuthTab canceled"
            AuthTabIntent.RESULT_VERIFICATION_FAILED -> "Verification failed"
            AuthTabIntent.RESULT_VERIFICATION_TIMED_OUT -> "Verification timed out"
            else -> "Some other result"
        }

        Log.d(tag, "Auth result: $authResult")
        if (result.resultCode != AuthTabIntent.RESULT_OK) return@rememberLauncherForActivityResult

        // Extract the authorization code from the redirect and then exchange it for an ID token
        val authCode = result.resultUri?.getQueryParameter("code")
        if (authCode == null) {
            Log.e(tag, "No auth code received")
            return@rememberLauncherForActivityResult
        }

        Log.d(tag, "Login with OAuth :: code = $authCode, provider = $provider")
//        profileViewModel.loginWithOAuth(authCode, provider)
    }

    Button(
        onClick = {
            if (authUrl == null) return@Button
            val authUri = Uri.parse(authUrl)
            val redirectUri = Uri.parse(Constants.REDIRECT_URL)
            val host = redirectUri.host
            val path = redirectUri.path
            if (host == null || path == null) return@Button

            // Start the authorization code flow
            // Check if the default browser supports auth or custom tabs
            val defaultBrowser = CustomTabsClient.getPackageName(context, listOf())
            if (defaultBrowser == null) {
                Toast.makeText(
                    context,
                    "OAuth login not supported by the default browser",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (CustomTabsClient.isAuthTabSupported(context, defaultBrowser)) {
                Log.d(tag, "Launching auth tab")
                val authTabIntent = AuthTabIntent.Builder()
                    .setEphemeralBrowsingEnabled(true) // ephemeral = don't save cookies
                    .build()
                authTabIntent.launch(launcher, authUri, host, path)
            } else {
                Log.d(tag, "Launching custom tab")
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setEphemeralBrowsingEnabled(true)
                    .build()
                customTabsIntent.launchUrl(context, authUri)
            }
        },
        enabled = authUrl != null,
        colors = ButtonDefaults.buttonColors(
            containerColor = provider.style.backgroundColor,
            contentColor = provider.style.contentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(provider.style.icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = provider.style.label,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun OAuthButtonPreview() {
    val context = LocalContext.current

    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = viewModel {
        ProfileViewModel(
            chefRepository = ChefRepository(chefService),
            recipeRepository = RecipeRepository(recipeService),
            dataStoreService = DataStoreService(context)
        )
    }

    EZRecipesTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                OAuthButton(
                    provider = Provider.GOOGLE,
                    authUrl = Constants.Mocks.AUTH_URLS[0].authUrl,
                    profileViewModel = profileViewModel
                )
                OAuthButton(
                    provider = Provider.FACEBOOK,
                    authUrl = Constants.Mocks.AUTH_URLS[1].authUrl,
                    profileViewModel = profileViewModel
                )
                OAuthButton(
                    provider = Provider.GITHUB,
                    authUrl = Constants.Mocks.AUTH_URLS[2].authUrl,
                    profileViewModel = profileViewModel
                )
                OAuthButton(
                    provider = Provider.GOOGLE,
                    profileViewModel = profileViewModel
                ) // disabled
            }
        }
    }
}
