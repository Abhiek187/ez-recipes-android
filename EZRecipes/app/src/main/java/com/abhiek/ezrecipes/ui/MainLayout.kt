package com.abhiek.ezrecipes.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.abhiek.ezrecipes.ui.navbar.NavigationDrawer
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.currentWindowSize

@Composable
fun MainLayout(
    widthSizeClass: WindowWidthSizeClass
) {
    // Remember functions can only be called in a composable, not an activity
    val scope = rememberCoroutineScope()

    // Material Design layout guidelines:
    // https://developer.android.com/guide/topics/large-screens/navigation-for-responsive-uis#responsive_ui_navigation
    EZRecipesTheme {
        // Show the navigation drawer on large screens
        if (widthSizeClass == WindowWidthSizeClass.Expanded) {
            NavigationDrawer(scope, widthSizeClass)
        } else {
            MainScaffold(scope, widthSizeClass)
        }
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
    MainLayout(windowSize.widthSizeClass)
}
