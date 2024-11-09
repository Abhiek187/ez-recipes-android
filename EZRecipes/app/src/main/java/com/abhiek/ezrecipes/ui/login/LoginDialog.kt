package com.abhiek.ezrecipes.ui.login

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Routes

@Composable
fun LoginDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // Occupy full screen width
            decorFitsSystemWindows = false // Allow custom layout around system bars
        )
    ) {
        // Use a NavHost to manage navigation within the dialog
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Routes.LOGIN) {
            composable(Routes.LOGIN) { LoginForm(navController) }
            composable(Routes.SIGN_UP) { SignUpForm(navController) }
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun LoginDialogPreview() {
    EZRecipesTheme {
        Surface {
            LoginDialog {}
        }
    }
}
