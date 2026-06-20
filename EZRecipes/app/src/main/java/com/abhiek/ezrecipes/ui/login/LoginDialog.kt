package com.abhiek.ezrecipes.ui.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.profile.PasskeyManager
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.rememberNavigationState
import com.abhiek.ezrecipes.ui.util.toEntries
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.Navigator
import com.abhiek.ezrecipes.utils.Routes

@Composable
fun LoginDialog(profileViewModel: ProfileViewModel, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        // Add a background so the dialog appears on top of the main content
        Surface(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.background
        ) {
            // Use a NavDisplay to manage navigation within the dialog
            val navigationState = rememberNavigationState(
                startRoute = Routes.Login,
                topLevelRoutes = Constants.LOGIN_ROUTES
            )
            val navigator = remember { Navigator(navigationState) }

            val entryProvider = entryProvider {
                entry<Routes.Login> {
                    LoginForm(
                        profileViewModel = profileViewModel,
                        onSignup = {
                            navigator.navigate(Routes.SignUp)
                        },
                        onForgotPassword = {
                            navigator.navigate(Routes.ForgotPassword)
                        },
                        onVerifyEmail = { email ->
                            navigator.navigate(Routes.VerifyEmail(email))
                        }
                    )
                }
                entry<Routes.SignUp> {
                    SignUpForm(
                        profileViewModel = profileViewModel,
                        onLogin = {
                            navigator.navigate(Routes.Login)
                        },
                        onVerifyEmail = { email ->
                            navigator.navigate(Routes.VerifyEmail(email))
                        }
                    )
                }
                entry<Routes.VerifyEmail> { key ->
                    VerifyEmail(
                        email = key.email,
                        onResend = {
                            profileViewModel.sendVerificationEmail()
                        },
                        onLogout = {
                            profileViewModel.logout()
                        }
                    )
                }
                entry<Routes.ForgotPassword> {
                    ForgotPasswordForm(profileViewModel)
                }
            }

            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() }
            )
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun LoginDialogPreview() {
    val context = LocalContext.current

    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = viewModel {
        ProfileViewModel(
            chefRepository = ChefRepository(chefService),
            recipeRepository = RecipeRepository(recipeService),
            dataStoreService = DataStoreService(context),
            passkeyManager = PasskeyManager(context)
        )
    }

    EZRecipesTheme {
        Surface {
            LoginDialog(profileViewModel) {}
        }
    }
}
