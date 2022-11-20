package com.abhiek.ezrecipes.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.RecipeRepository
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
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
                            Home()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Home(
    mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory()
    )) {
    val recipe by mainViewModel.recipe.observeAsState()
    val isLoading by mainViewModel.isLoading.observeAsState(initial = true)

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { mainViewModel.getRandomRecipe() },
            //modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(10),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow)
        ) {
            Text(
                text = "Find me a recipe!"
            )
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun HomePreview() {
    val viewModel = MainViewModel(RecipeRepository(MockRecipeService))

    EZRecipesTheme {
        Home(viewModel)
    }
}
