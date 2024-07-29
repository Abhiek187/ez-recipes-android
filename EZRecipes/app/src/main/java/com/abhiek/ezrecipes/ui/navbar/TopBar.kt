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
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.currentWindowSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scope: CoroutineScope,
    navController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    drawerState: DrawerState? = null
) {
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
    val navController = rememberNavController()
    val windowSize = currentWindowSize()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    EZRecipesTheme {
        TopBar(scope, navController, windowSize.widthSizeClass, drawerState)
    }
}
