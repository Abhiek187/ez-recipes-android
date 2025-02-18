package com.abhiek.ezrecipes.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Chef
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.ErrorAlert
import com.abhiek.ezrecipes.utils.Routes
import com.abhiek.ezrecipes.utils.toShorthand

@Composable
fun ProfileLoggedIn(
    chef: Chef,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current

    var dialogToShow by remember { mutableStateOf<String?>(null) }

    val onDismiss = {
        dialogToShow = null
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
                text = context.resources.getQuantityString(
                    R.plurals.favorites, numFavoriteRecipes, numFavoriteRecipes.toShorthand()
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            VerticalDivider()
            Text(
                text = context.resources.getQuantityString(
                    R.plurals.recipes_viewed, numViewedRecipes, numViewedRecipes.toShorthand()
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            VerticalDivider()
            Text(
                text = context.resources.getQuantityString(
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

        if (profileViewModel.showAlert) {
            ErrorAlert(
                message = profileViewModel.recipeError?.error,
                onDismiss = {
                    profileViewModel.showAlert = false
                }
            )
        }

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
    }
}

private data class ProfileLoggedInState(
    val isLoading: Boolean
)

private class ProfileLoggedInPreviewParameterProvider:
    PreviewParameterProvider<ProfileLoggedInState> {
    override val values = sequenceOf(
        ProfileLoggedInState(isLoading = false),
        ProfileLoggedInState(isLoading = true)
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
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService),
        dataStoreService = DataStoreService(context)
    )
    profileViewModel.chef = chefService.chef
    profileViewModel.isLoading = state.isLoading

    EZRecipesTheme {
        Surface {
            ProfileLoggedIn(chefService.chef, profileViewModel)
        }
    }
}
