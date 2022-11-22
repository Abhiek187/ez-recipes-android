package com.abhiek.ezrecipes.utils

object Constants {
    // In Retrofit, base URLs must end with a /
    const val RECIPE_BASE_URL = "https://ez-recipes-server.onrender.com/api/recipes/"
    const val RANDOM_RECIPE_PATH = "random"

    // Routes in the navigation graph
    object Routes {
        const val HOME = "home"
        const val RECIPE = "recipe/{recipe}"
    }
}
