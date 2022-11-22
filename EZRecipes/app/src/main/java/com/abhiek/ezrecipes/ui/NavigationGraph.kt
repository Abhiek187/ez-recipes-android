package com.abhiek.ezrecipes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.home.Home
import com.abhiek.ezrecipes.ui.recipe.Recipe
import com.abhiek.ezrecipes.utils.Constants
import com.google.gson.Gson
import java.net.URLEncoder

@Composable
fun NavigationGraph() {
    // The navigation controller shouldn't be recreated in other composables
    val navController = rememberNavController()

    // Show the appropriate composable based on the current route, starting at the home screen
    NavHost(
        navController = navController,
        startDestination = Constants.Routes.HOME
    ) {
        composable(Constants.Routes.HOME) {
            Home { recipe ->
                // Store the recipe as a string in the route
                val recipeJson = Gson().toJson(recipe)
                // Encode the string to prevent errors with parsing URLs
                var encodedRecipeJson = URLEncoder.encode(recipeJson, Charsets.UTF_8.name())
                // Encode spaces with %20 instead of + to decode the GSON properly
                encodedRecipeJson = encodedRecipeJson.replace("+", "%20")

                navController.navigate(
                    Constants.Routes.RECIPE.replace("{recipe}", encodedRecipeJson)
                ) {
                    // Only have one copy of the recipe destination in the back stack
                    launchSingleTop = true
                }
            }
        }
        composable(Constants.Routes.RECIPE) { backStackEntry ->
            // Parse the recipe object from the route string
            val recipeJson = backStackEntry.arguments?.getString("recipe")
            val recipe = Gson().fromJson(recipeJson, Recipe::class.java)
            Recipe(recipe)
        }
    }
}
