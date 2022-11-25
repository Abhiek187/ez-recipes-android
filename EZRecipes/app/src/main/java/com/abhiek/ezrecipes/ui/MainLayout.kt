package com.abhiek.ezrecipes.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.ui.navbar.NavigationDrawer
import com.abhiek.ezrecipes.ui.navbar.NavigationGraph
import com.abhiek.ezrecipes.ui.navbar.TopBar
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun MainLayout(initialDrawerState: DrawerValue = DrawerValue.Open) {
    // Remember functions can only be called in a composable, not an activity
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialDrawerState))
    // The navigation controller shouldn't be recreated in other composables
    val navController = rememberNavController()

    // Great resource for setting up the navigation drawer: https://stackoverflow.com/a/73295465
    EZRecipesTheme {
        Scaffold (
            scaffoldState = scaffoldState,
            topBar = { TopBar(scope, scaffoldState) },
            drawerContent = {
                NavigationDrawer(scope, scaffoldState, navController)
            },
            // Content padding parameter is required: https://stackoverflow.com/a/72085218
            content = { padding ->
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.padding(padding),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationGraph(navController)
                }
            }
        )
    }
}

private class MainLayoutPreviewParameterProvider: PreviewParameterProvider<DrawerValue> {
    // Show a preview of the drawer when opened and closed
    override val values = sequenceOf(DrawerValue.Open, DrawerValue.Closed)
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun MainLayoutPreview(
    @PreviewParameter(MainLayoutPreviewParameterProvider::class) drawerState: DrawerValue
) {
    EZRecipesTheme {
        MainLayout(drawerState)
    }
}
