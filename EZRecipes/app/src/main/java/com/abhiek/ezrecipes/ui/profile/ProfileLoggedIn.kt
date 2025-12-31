package com.abhiek.ezrecipes.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Chef
import com.abhiek.ezrecipes.data.models.Provider
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.login.LoginForm
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.ErrorAlert
import com.abhiek.ezrecipes.ui.util.OAuthButton
import com.abhiek.ezrecipes.utils.Routes
import com.abhiek.ezrecipes.utils.toShorthand

@Composable
fun ProfileLoggedIn(
    chef: Chef,
    profileViewModel: ProfileViewModel
) {
    val resources = LocalResources.current
    val context = LocalContext.current

    var dialogToShow by remember { mutableStateOf<String?>(null) }
    var selectedProvider by remember { mutableStateOf(Provider.GOOGLE) }
    var showUnlinkConfirmation by remember { mutableStateOf(false) }

    val linkedAccounts = remember(chef.providerData) {
        // Start with all the supported providers
        val initialResult = Provider.entries.associateWith { mutableListOf<String>() }
        // A chef can link 0 or more emails with a provider
        chef.providerData.fold(initialResult) { result, providerData ->
            Provider.valueOfOrNull(providerData.providerId)?.let { providerId ->
                result[providerId]?.add(providerData.email)
            }

            return@fold result
        }
    }

    val onDismiss = {
        dialogToShow = null
    }
    val dismissUnlinkConfirmation = {
        showUnlinkConfirmation = false
    }

    LaunchedEffect(Unit) {
        profileViewModel.getAuthUrls()
    }
    LaunchedEffect(profileViewModel.accountLinked) {
        if (profileViewModel.accountLinked) {
            Toast.makeText(
                context,
                resources.getString(R.string.link_success, selectedProvider),
                Toast.LENGTH_SHORT
            ).show()
            profileViewModel.accountLinked = false
        }
    }
    LaunchedEffect(profileViewModel.accountUnlinked) {
        if (profileViewModel.accountUnlinked) {
            Toast.makeText(
                context,
                resources.getString(R.string.unlink_success, selectedProvider),
                Toast.LENGTH_SHORT
            ).show()
            profileViewModel.accountUnlinked = false
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.profile_header, chef.email),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                // Set the height to make the dividers visible
                .height(IntrinsicSize.Min)
        ) {
            val numFavoriteRecipes = chef.favoriteRecipes.size
            val numViewedRecipes = chef.recentRecipes.size
            val numRatedRecipes = chef.ratings.size

            Text(
                text = resources.getQuantityString(
                    R.plurals.favorites, numFavoriteRecipes, numFavoriteRecipes.toShorthand()
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            VerticalDivider()
            Text(
                text = resources.getQuantityString(
                    R.plurals.recipes_viewed, numViewedRecipes, numViewedRecipes.toShorthand()
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            VerticalDivider()
            Text(
                text = resources.getQuantityString(
                    R.plurals.total_ratings, numRatedRecipes, numRatedRecipes.toShorthand()
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Button(
            onClick = {
                profileViewModel.logout()
            },
            enabled = !profileViewModel.isLoading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.logout))
                if (profileViewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
        Button(
            onClick = {
                dialogToShow = Routes.UPDATE_EMAIL
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.change_email))
        }
        Button(
            onClick = {
                dialogToShow = Routes.UPDATE_PASSWORD
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.change_password))
        }
        Button(
            onClick = {
                dialogToShow = Routes.DELETE_ACCOUNT
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.delete_account),
                color = MaterialTheme.colorScheme.onError
            )
        }

        HorizontalDivider()
        Text(
            text = stringResource(R.string.linked_accounts),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth()
        )
        linkedAccounts.entries.forEach { (provider, emails) ->
            key(provider) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OAuthButton(
                        provider = provider,
                        authUrl = profileViewModel.authUrls[provider],
                        profileViewModel = profileViewModel
                    )
                    Button(
                        onClick = {
                            // Confirm before unlinking
                            selectedProvider = provider
                            showUnlinkConfirmation = true
                        },
                        enabled = emails.isNotEmpty() && !profileViewModel.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.unlink),
                                color = if (emails.isEmpty() || profileViewModel.isLoading) {
                                    Color.Unspecified
                                } else {
                                    MaterialTheme.colorScheme.onError
                                }
                            )
                            if (profileViewModel.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
                if (emails.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.Green
                        )
                        Text(
                            text = emails.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (profileViewModel.showAlert && !showUnlinkConfirmation) {
            ErrorAlert(
                message = profileViewModel.recipeError?.error,
                onDismiss = {
                    profileViewModel.showAlert = false
                }
            )
        }
        if (showUnlinkConfirmation) {
            AlertDialog(
                onDismissRequest = dismissUnlinkConfirmation,
                text = {
                    Text(
                        text = stringResource(
                            R.string.unlink_confirmation,
                            selectedProvider.toString()
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            profileViewModel.unlinkOAuthProvider(selectedProvider)
                            dismissUnlinkConfirmation()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.yes_button),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = dismissUnlinkConfirmation
                    ) {
                        Text(
                            text = stringResource(R.string.no_button)
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        if (!profileViewModel.openLoginDialog) {
            dialogToShow?.let { dialog ->
                Dialog(
                    onDismissRequest = onDismiss
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (dialog) {
                            Routes.UPDATE_EMAIL ->
                                UpdateEmailForm(profileViewModel)

                            Routes.UPDATE_PASSWORD ->
                                UpdatePasswordForm(profileViewModel, onDismiss)

                            Routes.DELETE_ACCOUNT ->
                                DeleteAccountForm(profileViewModel, onDismiss)
                        }
                    }
                }
            }
        } else {
            Dialog(
                onDismissRequest = {
                    profileViewModel.openLoginDialog = false
                }
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginForm(
                        profileViewModel = profileViewModel,
                        isStepUp = true,
                        onLogin = {
                            // After logging in, go back to the previous dialog
                            profileViewModel.openLoginDialog = false
                            dialogToShow = Routes.UPDATE_EMAIL
                        }
                    )
                }
            }
        }
    }
}

private data class ProfileLoggedInState(
    val isLoading: Boolean = false,
    val accountLinked: Boolean = false,
    val accountUnlinked: Boolean = false
)

private class ProfileLoggedInPreviewParameterProvider:
    PreviewParameterProvider<ProfileLoggedInState> {
    override val values = sequenceOf(
        ProfileLoggedInState(),
        ProfileLoggedInState(isLoading = true),
        ProfileLoggedInState(accountLinked = true),
        ProfileLoggedInState(accountUnlinked = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ProfileLoggedInPreview(
    @PreviewParameter(ProfileLoggedInPreviewParameterProvider::class) state: ProfileLoggedInState
) {
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
    profileViewModel.chef = chefService.chef
    profileViewModel.isLoading = state.isLoading
    profileViewModel.accountLinked = state.accountLinked
    profileViewModel.accountUnlinked = state.accountUnlinked

    EZRecipesTheme {
        Surface {
            ProfileLoggedIn(chefService.chef, profileViewModel)
        }
    }
}
