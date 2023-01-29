package com.abhiek.ezrecipes.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
fun MainLayout(
    widthSizeClass: WindowWidthSizeClass,
    initialDrawerState: DrawerValue = DrawerValue.Closed
) {
    // Remember functions can only be called in a composable, not an activity
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialDrawerState))
    // The navigation controller shouldn't be recreated in other composables
    val navController = rememberNavController()

    // Material Design layout guidelines:
    // https://developer.android.com/guide/topics/large-screens/navigation-for-responsive-uis#responsive_ui_navigation
    EZRecipesTheme {
        // Great resource for setting up the navigation drawer: https://stackoverflow.com/a/73295465
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { TopBar(scope, scaffoldState, navController) },
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
                    NavigationGraph(navController, widthSizeClass)
                }
            }
        )
    }
}

private data class MainLayoutState(
    val widthSizeClass: WindowWidthSizeClass,
    val drawerState: DrawerValue
)

private class MainLayoutPreviewParameterProvider: PreviewParameterProvider<MainLayoutState> {
    // Show a preview of the drawer when opened and closed based on the screen width
    override val values = sequenceOf(
        MainLayoutState(widthSizeClass = WindowWidthSizeClass.Compact, drawerState = DrawerValue.Closed),
        MainLayoutState(widthSizeClass = WindowWidthSizeClass.Medium, drawerState = DrawerValue.Closed),
        MainLayoutState(widthSizeClass = WindowWidthSizeClass.Expanded, drawerState = DrawerValue.Open)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun MainLayoutPreview(
    @PreviewParameter(MainLayoutPreviewParameterProvider::class) state: MainLayoutState
) {
    EZRecipesTheme {
        MainLayout(state.widthSizeClass, state.drawerState)
    }
}
