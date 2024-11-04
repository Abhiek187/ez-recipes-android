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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Chef
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.search.RecipeCard
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

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
            style = MaterialTheme.typography.headlineMedium.copy(
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Accordion(
            header = stringResource(R.string.profile_favorites),
            expandByDefault = false
        ) {
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

        Accordion(
            header = stringResource(R.string.profile_recently_viewed),
            expandByDefault = false
        ) {
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

        Accordion(
            header = stringResource(R.string.profile_ratings),
            expandByDefault = false
        ) {
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

        Button(
            onClick = { println("Logout") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.logout))
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
            Text(text = stringResource(R.string.delete_account))
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ProfileLoggedInPreview() {
    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService)
    )
    profileViewModel.chef = chefService.chef

    EZRecipesTheme {
        Surface {
            ProfileLoggedIn(chefService.chef, profileViewModel)
        }
    }
}
