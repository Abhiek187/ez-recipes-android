package com.abhiek.ezrecipes.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            EZRecipesTheme {
                Scaffold (
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = stringResource(R.string.app_name))
                            },
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    },
                    // Content padding parameter is required: https://stackoverflow.com/a/72085218
                    content = { padding ->
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.padding(padding),
                            color = MaterialTheme.colors.background
                        ) {
                            NavigationGraph()
                        }
                    }
                )
            }
        }
    }
}
