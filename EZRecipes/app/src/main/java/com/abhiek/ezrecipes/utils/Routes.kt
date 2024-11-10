package com.abhiek.ezrecipes.utils

object Routes {
    // tabs = user-facing labels, routes = internal "pretend" URL paths
    // All tabs have routes, but not all routes have tabs
    const val HOME = "home"
    const val RECIPE = "recipe/{id}"
    const val SEARCH = "search"
    const val RESULTS = "search/results"
    const val GLOSSARY = "glossary"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val SIGN_UP = "sign-up"
    const val VERIFY_EMAIL = "verify/{email}"
    const val FORGOT_PASSWORD = "forgot-password"
}
