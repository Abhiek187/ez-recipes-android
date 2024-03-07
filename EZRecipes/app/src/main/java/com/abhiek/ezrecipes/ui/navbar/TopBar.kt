package com.abhiek.ezrecipes.ui.navbar

import android.content.ClipDescription
import android.content.Intent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {
    var isFavorite by remember { mutableStateOf(false) }
    // Get a context variable like in activities
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    fun shareRecipe(id: String) {
        // Create a Sharesheet to share the recipe with others
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_body))
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
            // Show a hamburger menu in the top left
            IconButton(
                onClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            ) {
                Icon(Icons.Filled.Menu, stringResource(R.string.hamburger_menu_alt))
            }
        },
        // Add a favorite and share button on the right side if we're on the recipe screen
        actions = {
            if (currentRoute == Constants.Routes.RECIPE) {
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        if (isFavorite) stringResource(R.string.un_favorite_alt) else stringResource(
                            R.string.favorite_alt
                        )
                    )
                }
                IconButton(onClick = {
                    navBackStackEntry?.arguments?.getString("id")?.let { recipeId ->
                        shareRecipe(recipeId)
                    }
                }) {
                    Icon(Icons.Filled.Share, stringResource(R.string.share_alt))
                }
            }
        },
        backgroundColor = MaterialTheme.colors.primary
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun TopBarPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val navController = rememberNavController()

    EZRecipesTheme {
        TopBar(scope, scaffoldState, navController)
    }
}
