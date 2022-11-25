package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    navController: NavController
) {
    /* Using sealedSubclasses requires reflection, which will make the app slower,
     * so list each drawer item manually
     */
    val drawerItems = listOf(
        DrawerItem.Home
    )

    Column {
        // Show the app logo at the top
//        Image(
//            painter = painterResource(R.mipmap.ic_launcher_foreground),
//            contentDescription = stringResource(R.string.app_logo_alt),
//            modifier = Modifier
//                .height(100.dp)
//                .fillMaxWidth()
//                .padding(8.dp)
//        )
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(8.dp)
//        )
        // Loop through all the subclasses of the sealed DrawerItem class
        drawerItems.forEach { item ->
            // Show text that can be clicked on to navigate to the appropriate screen
            ClickableText(
                text = AnnotatedString(item.title),
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
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
        NavigationDrawer(scope, scaffoldState, navController)
    }
}
