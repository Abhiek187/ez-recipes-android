package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.MainScaffold
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.currentWindowSize
import com.abhiek.ezrecipes.utils.toPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    scope: CoroutineScope,
    navController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    width: Int = 300,
    initialDrawerValue: DrawerValue = DrawerValue.Closed
) {
    /* Using sealedSubclasses requires reflection, which will make the app slower,
     * so list each drawer item manually
     */
    val drawerItems = listOf(Tab.Home, Tab.Search, Tab.Glossary)
    val drawerState = rememberDrawerState(initialDrawerValue)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Keep the logo centered within the drawer (left padding <-> logo <-> right padding)
    val logoWidth = 100
    val logoHorizontalPadding = (width - logoWidth) / 2

    LaunchedEffect(widthSizeClass) {
        // Close the navigation drawer if the screen is too small
        if (widthSizeClass != WindowWidthSizeClass.Expanded && drawerState.isOpen) {
            drawerState.close()
        }
    }

    // Great resource for setting up the navigation drawer: https://stackoverflow.com/a/73295465
    ModalNavigationDrawer(
        drawerState = drawerState,
        // ModalNavigationDrawer.drawerContent.ModalDrawerSheet.content = UI inside drawer
        // ModalNavigationDrawer.content = UI outside drawer
        drawerContent = {
            ModalDrawerSheet(
                // Limit the width of the navigation drawer so there isn't much whitespace
                drawerShape = object : Shape {
                    override fun createOutline(
                        size: Size,
                        layoutDirection: LayoutDirection,
                        density: Density
                    ) = Outline.Rectangle(
                        Rect(left = 0f, top = 0f, right = width.toPx, bottom = size.height)
                    )
                },
            ) {
                Column {
                    // Show the app logo at the top
                    Image(
                        painter = painterResource(R.mipmap.ic_launcher_foreground),
                        contentDescription = stringResource(R.string.app_logo_alt),
                        modifier = Modifier
                            .height(logoWidth.dp)
                            .padding(horizontal = logoHorizontalPadding.dp, vertical = 8.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    // Loop through all the subclasses of the sealed Tab class
                    drawerItems.forEach { item ->
                        DrawerListItem(
                            item = item,
                            // Highlight the drawer item corresponding to the current route on screen
                            selected = currentRoute == item.route,
                            onItemClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        MainScaffold(scope, navController, widthSizeClass, drawerState)
    }
}

private class NavigationDrawerPreviewParameterProvider: PreviewParameterProvider<DrawerValue> {
    override val values = sequenceOf(DrawerValue.Closed, DrawerValue.Open)
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun NavigationDrawerPreview(
    @PreviewParameter(NavigationDrawerPreviewParameterProvider::class) drawerValue: DrawerValue
) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val windowSize = currentWindowSize()

    EZRecipesTheme {
        Surface {
            NavigationDrawer(
                scope,
                navController,
                windowSize.widthSizeClass,
                initialDrawerValue = drawerValue
            )
        }
    }
}
