package com.abhiek.ezrecipes.utils

import android.content.res.Resources
import com.abhiek.ezrecipes.R

object Constants {
    // In Retrofit, base URLs must end with a /
    const val RECIPE_BASE_URL = "https://ez-recipes-server.onrender.com/api/recipes/"
    const val RANDOM_RECIPE_PATH = "random"

    // Error message to fallback on in case all fails
    val UNKNOWN_ERROR = Resources.getSystem().getString(R.string.unknown_error)

    // Routes in the navigation graph
    object Routes {
        const val HOME = "home"
        const val RECIPE = "recipe/{recipe}"
    }
}
