package com.abhiek.ezrecipes.ui.navbar

import android.content.ClipDescription
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
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
import com.abhiek.ezrecipes.ui.util.LocalNavigationState
import com.abhiek.ezrecipes.ui.util.rememberNavigationState
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.Routes
import com.abhiek.ezrecipes.utils.currentWindowSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scope: CoroutineScope,
    widthSizeClass: WindowWidthSizeClass,
    drawerState: DrawerState? = null,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val navigationState = LocalNavigationState.current

    val navBackStackEntry = navigationState.backStacks[navigationState.topLevelRoute]?.last()
    val isRecipeRoute = navBackStackEntry is Routes.Recipe
    val recipeId = if (isRecipeRoute) navBackStackEntry.id else null

    val isFavorite = profileViewModel.chef?.favoriteRecipes?.contains(recipeId.toString()) == true

    // Check if the recipe is one of the chef's favorites
    LaunchedEffect(Unit) {
        profileViewModel.getChef()
    }

    fun shareRecipe(id: Int) {
        // Create a Sharesheet to share the recipe with others
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_body))
            putExtra(Intent.EXTRA_TEXT, "${Constants.RECIPE_WEB_ORIGIN}/recipe/$id")
            type = ClipDescription.MIMETYPE_TEXT_PLAIN
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    TopAppBar(
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        navigationIcon = {
            if (widthSizeClass == WindowWidthSizeClass.Expanded) {
                // Show a hamburger menu in the top left
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState?.open()
                        }
                    }
                ) {
                    Icon(Icons.Filled.Menu, stringResource(R.string.hamburger_menu_alt))
                }
            }
        },
        // Add a favorite and share button on the right side if we're on the recipe screen
        actions = {
            if (isRecipeRoute && recipeId != null) {
                IconButton(
                    onClick = {
                        profileViewModel.toggleFavoriteRecipe(recipeId, !isFavorite)
                    },
                    enabled = profileViewModel.chef != null
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = if (isFavorite) {
                            stringResource(R.string.un_favorite_alt)
                        } else {
                            stringResource(R.string.favorite_alt)
                        }
                    )
                }
                IconButton(onClick = {
                    shareRecipe(recipeId)
                }) {
                    Icon(Icons.Filled.Share, stringResource(R.string.share_alt))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun TopBarPreview() {
    val scope = rememberCoroutineScope()
    val windowSize = currentWindowSize()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navigationState = rememberNavigationState()

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

    CompositionLocalProvider(LocalNavigationState provides navigationState) {
        EZRecipesTheme {
            TopBar(scope, windowSize.widthSizeClass, drawerState, profileViewModel)
        }
    }
}
