package com.abhiek.ezrecipes.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.ui.navbar.*
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.currentWindowSize
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainScaffold(
    scope: CoroutineScope,
    navController: NavHostController,
    widthSizeClass: WindowWidthSizeClass
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(scope, navController, widthSizeClass)
        },
        // Show the navigation bar on small screens
        bottomBar = {
            if (widthSizeClass == WindowWidthSizeClass.Compact) {
                BottomBar(navController)
            }
        },
        // Content padding parameter is required: https://stackoverflow.com/a/72085218
        content = { padding ->
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                Row {
                    // Show the navigation rail on medium screens
                    if (widthSizeClass == WindowWidthSizeClass.Medium) {
                        NavRail(navController)
                    }

                    NavigationGraph(navController, widthSizeClass)
                }
            }
        }
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun MainScaffoldPreview() {
    val scope = rememberCoroutineScope()
    // Use the actual size of the device to show accurate previews
    val windowSize = currentWindowSize()
    val navController = rememberNavController()

    EZRecipesTheme {
        MainScaffold(scope, navController, windowSize.widthSizeClass)
    }
}
