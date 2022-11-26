package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.home.Home
import com.abhiek.ezrecipes.ui.recipe.Recipe
import com.google.gson.Gson
import java.net.URLEncoder

@Composable
fun NavigationGraph(navController: NavHostController) {
    // Show the appropriate composable based on the current route, starting at the home screen
    // NavHostController is a subclass of NavController
    NavHost(
        navController = navController,
        startDestination = DrawerItem.Home.route
    ) {
        composable(DrawerItem.Home.route) {
            Home { recipe ->
                // Store the recipe as a string in the route
                val recipeJson = Gson().toJson(recipe)
                // Encode the string to prevent errors with parsing URLs
                var encodedRecipeJson = URLEncoder.encode(recipeJson, Charsets.UTF_8.name())
                // Encode spaces with %20 instead of + to decode the GSON properly
                encodedRecipeJson = encodedRecipeJson.replace("+", "%20")

                navController.navigate(
                    DrawerItem.Recipe.route.replace("{recipe}", encodedRecipeJson)
                ) {
                    // Only have one copy of the recipe destination in the back stack
                    launchSingleTop = true
                }
            }
        }
        composable(DrawerItem.Recipe.route) { backStackEntry ->
            // Parse the recipe object from the route string
            val recipeJson = backStackEntry.arguments?.getString("recipe")
            val recipe = Gson().fromJson(recipeJson, Recipe::class.java)
            Recipe(recipe)
        }
    }
}
