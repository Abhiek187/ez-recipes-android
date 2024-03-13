package com.abhiek.ezrecipes.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.ui.navbar.*
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.currentWindowSize
import com.abhiek.ezrecipes.utils.toPx

@Composable
fun MainLayout(
    widthSizeClass: WindowWidthSizeClass
) {
    // Remember functions can only be called in a composable, not an activity
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    // The navigation controller shouldn't be recreated in other composables
    val navController = rememberNavController()
    val drawerWidth = 300

    LaunchedEffect(widthSizeClass) {
        // Close the navigation drawer if the screen is too small
        if (widthSizeClass != WindowWidthSizeClass.Expanded && scaffoldState.drawerState.isOpen) {
            scaffoldState.drawerState.close()
        }
    }

    // Material Design layout guidelines:
    // https://developer.android.com/guide/topics/large-screens/navigation-for-responsive-uis#responsive_ui_navigation
    EZRecipesTheme {
        // Great resource for setting up the navigation drawer: https://stackoverflow.com/a/73295465
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopBar(scope, scaffoldState, navController, widthSizeClass)
            },
            // Show the navigation bar on small screens
            bottomBar = {
                if (widthSizeClass == WindowWidthSizeClass.Compact) {
                    BottomBar(navController)
                }
            },
            // Show the navigation drawer on large screens
            drawerContent = if (widthSizeClass == WindowWidthSizeClass.Expanded) {
                { NavigationDrawer(scope, scaffoldState, navController, drawerWidth) }
            } else null,
            // Limit the width of the navigation drawer so there isn't much whitespace
            drawerShape = object : Shape {
                override fun createOutline(
                    size: Size,
                    layoutDirection: LayoutDirection,
                    density: Density
                ) = Outline.Rectangle(
                    Rect(left = 0f, top = 0f, right = drawerWidth.toPx, bottom = size.height)
                )
            },
            // Content padding parameter is required: https://stackoverflow.com/a/72085218
            content = { padding ->
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.padding(padding),
                    color = MaterialTheme.colors.background
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
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun MainLayoutPreview() {
    // Use the actual size of the device to show accurate previews
    val windowSize = currentWindowSize()

    EZRecipesTheme {
        MainLayout(windowSize.widthSizeClass)
    }
}
