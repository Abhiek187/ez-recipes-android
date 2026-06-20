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
fun NavRail() {
    val navigationState = LocalNavigationState.current
    val navigator = LocalNavigator.current

    NavigationRail {
        Constants.TABS.forEach { tab ->
            NavigationRailItem(
                label = { Text(stringResource(tab.resourceId)) },
                icon = { Icon(tab.icon, contentDescription = null) },
                selected = tab.route::class == navigationState.topLevelRoute::class,
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
fun NavRailPreview() {
    val navigationState = rememberNavigationState()
    val navigator = remember { Navigator(navigationState) }

    CompositionLocalProvider(
        LocalNavigationState provides navigationState,
        LocalNavigator provides navigator
    ) {
        EZRecipesTheme {
            Surface {
                NavRail()
            }
        }
    }
}
