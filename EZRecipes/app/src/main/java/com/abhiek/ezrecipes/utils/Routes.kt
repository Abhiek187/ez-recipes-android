package com.abhiek.ezrecipes.utils

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object Routes {
    // tabs = user-facing labels, routes = internal "pretend" URL paths
    // All tabs have routes, but not all routes have tabs
    @Serializable data object Home: NavKey
    @Serializable data class Recipe(val id: Int): NavKey

    @Serializable data object Search: NavKey
    @Serializable data object Results: NavKey

    @Serializable data object Glossary: NavKey

    @Serializable data class Profile(val action: String? = null): NavKey
    @Serializable data object Login: NavKey
    @Serializable data object SignUp: NavKey
    @Serializable data class VerifyEmail(val email: String): NavKey
    @Serializable data object ForgotPassword: NavKey
    @Serializable data object UpdateEmail: NavKey
    @Serializable data object UpdatePassword: NavKey
    @Serializable data object DeleteAccount: NavKey
}
