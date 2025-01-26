package com.abhiek.ezrecipes.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
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
            // Use a NavHost to manage navigation within the dialog
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Routes.LOGIN) {
                composable(Routes.LOGIN) {
                    LoginForm(
                        profileViewModel = profileViewModel,
                        onSignup = {
                            navController.navigate(Routes.SIGN_UP) {
                                // Close the modal whenever the user navigates back
                                popUpTo(
                                    navController.currentBackStackEntry?.destination?.route
                                        ?: return@navigate
                                ) {
                                    inclusive =  true
                                }
                                launchSingleTop = true
                            }
                        },
                        onForgotPassword = {
                            navController.navigate(Routes.FORGOT_PASSWORD) {
                                popUpTo(
                                    navController.currentBackStackEntry?.destination?.route
                                        ?: return@navigate
                                ) {
                                    inclusive =  true
                                }
                                launchSingleTop = true
                            }
                        },
                        onVerifyEmail = { email ->
                            navController.navigate(
                                Routes.VERIFY_EMAIL.replace("{email}", email)
                            ) {
                                popUpTo(
                                    navController.currentBackStackEntry?.destination?.route
                                        ?: return@navigate
                                ) {
                                    inclusive =  true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Routes.SIGN_UP) {
                    SignUpForm(
                        profileViewModel = profileViewModel,
                        onLogin = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(
                                    navController.currentBackStackEntry?.destination?.route
                                        ?: return@navigate
                                ) {
                                    inclusive =  true
                                }
                                launchSingleTop = true
                            }
                        },
                        onVerifyEmail = { email ->
                            navController.navigate(
                                Routes.VERIFY_EMAIL.replace("{email}", email)
                            ) {
                                popUpTo(
                                    navController.currentBackStackEntry?.destination?.route
                                        ?: return@navigate
                                ) {
                                    inclusive =  true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Routes.VERIFY_EMAIL) { backStackEntry ->
                    VerifyEmail(
                        email = backStackEntry.arguments?.getString("email"),
                        onResend = {
                            profileViewModel.sendVerificationEmail()
                        },
                        onLogout = {
                            profileViewModel.logout()
                        }
                    )
                }
                composable(Routes.FORGOT_PASSWORD) {
                    ForgotPasswordForm(profileViewModel)
                }
            }
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
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService),
        dataStoreService = DataStoreService(context)
    )

    EZRecipesTheme {
        Surface {
            LoginDialog(profileViewModel) {}
        }
    }
}
