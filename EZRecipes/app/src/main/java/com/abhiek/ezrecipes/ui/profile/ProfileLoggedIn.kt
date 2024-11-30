package com.abhiek.ezrecipes.ui.profile

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
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
import com.abhiek.ezrecipes.ui.search.RecipeCard
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.Accordion
import com.abhiek.ezrecipes.ui.util.ErrorAlert

@Composable
fun ProfileLoggedIn(chef: Chef, profileViewModel: ProfileViewModel) {
    LaunchedEffect(Unit) {
        // Start loading all the recipe cards
        profileViewModel.getAllChefRecipes()
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

        Accordion(
            header = stringResource(R.string.profile_favorites),
            expandByDefault = false
        ) {
            // TODO: change to a loading condition
            if (true) {
                RecipeCardLoader()
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    for (recipe in profileViewModel.favoriteRecipes) {
                        RecipeCard(
                            recipe = recipe,
                            width = 350.dp
                        ) {}
                    }
                }
            }
        }

        Accordion(
            header = stringResource(R.string.profile_recently_viewed),
            expandByDefault = false
        ) {
            if (true) {
                RecipeCardLoader()
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    for (recipe in profileViewModel.recentRecipes) {
                        RecipeCard(
                            recipe = recipe,
                            width = 350.dp
                        ) {}
                    }
                }
            }
        }

        Accordion(
            header = stringResource(R.string.profile_ratings),
            expandByDefault = false
        ) {
            if (true) {
                RecipeCardLoader()
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    for (recipe in profileViewModel.ratedRecipes) {
                        RecipeCard(
                            recipe = recipe,
                            width = 350.dp
                        ) {}
                    }
                }
            }
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
            onClick = { println("Change Email") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.change_email))
        }
        Button(
            onClick = { println("Change Password") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.change_password))
        }
        Button(
            onClick = { println("Delete Account") },
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
