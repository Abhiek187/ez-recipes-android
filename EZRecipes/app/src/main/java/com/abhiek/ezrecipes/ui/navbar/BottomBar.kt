package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.LocalNavigationState
import com.abhiek.ezrecipes.ui.util.rememberNavigationState
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.LocalNavigator
import com.abhiek.ezrecipes.utils.Navigator

@Composable
fun BottomBar() {
    val navigationState = LocalNavigationState.current
    val navigator = LocalNavigator.current

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Constants.TABS.forEach { tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(stringResource(tab.resourceId)) },
                // Keep the tab selected as long as it matches one of the parent routes
                selected = tab.route::class == navigationState.topLevelRoute::class,
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    navigator.navigate(tab.route)
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
fun BottomBarPreview() {
    val navigationState = rememberNavigationState()
    val navigator = remember { Navigator(navigationState) }

    CompositionLocalProvider(
        LocalNavigationState provides navigationState,
        LocalNavigator provides navigator
    ) {
        EZRecipesTheme {
            BottomBar()
        }
    }
}
