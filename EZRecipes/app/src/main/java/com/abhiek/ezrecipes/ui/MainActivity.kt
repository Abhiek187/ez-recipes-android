package com.abhiek.ezrecipes.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.RecipeRepository
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
                    content = { padding ->
                        // A surface container using the 'background' color from the theme
                        Surface(
                            //modifier = Modifier.fillMaxSize(),
                            modifier = Modifier.padding(padding),
                            color = MaterialTheme.colors.background
                        ) {
                            Greeting("Android")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory()
    )) {
    val recipe by mainViewModel.recipe.observeAsState()
    //mainViewModel.getRandomRecipe()

    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = MainViewModel(RecipeRepository(MockRecipeService))

    EZRecipesTheme {
        Greeting("Android", viewModel)
    }
}
