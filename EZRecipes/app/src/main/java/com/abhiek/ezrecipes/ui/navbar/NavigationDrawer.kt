package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
    width: Int
) {
    /* Using sealedSubclasses requires reflection, which will make the app slower,
     * so list each drawer item manually
     */
    val drawerItems = listOf(
        DrawerItem.Home
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Keep the logo centered within the drawer (left padding <-> logo <-> right padding)
    val logoWidth = 100
    val logoHorizontalPadding = (width - logoWidth) / 2

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
        // Loop through all the subclasses of the sealed DrawerItem class
        drawerItems.forEach { item ->
            DrawerListItem(
                item = item,
                // Highlight the drawer item corresponding to the current route on screen
                selected = currentRoute == item.route,
                onItemClick = {
                    navController.navigate(item.route) {
                        // Pop all previous routes from the back stack until the selected route is found
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route)
                        }
                        launchSingleTop = true
                    }
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun NavigationDrawerPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val navController = rememberNavController()

    EZRecipesTheme {
        NavigationDrawer(scope, scaffoldState, navController, 300)
    }
}
