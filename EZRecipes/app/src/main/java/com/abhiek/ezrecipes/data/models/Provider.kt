package com.abhiek.ezrecipes.data.models

import androidx.compose.ui.graphics.Color
import com.abhiek.ezrecipes.R

enum class Provider(val style: ProviderStyle) {
    // Source: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/src/main/java/com/firebase/ui/auth/configuration/theme/ProviderStyleDefaults.kt
    GOOGLE(ProviderStyle(
        label = "Google",
        backgroundColor = Color.White,
        contentColor =  Color(0xFF757575),
        icon = R.drawable.fui_ic_googleg_color_24dp
    )),
    FACEBOOK(ProviderStyle(
        label = "Facebook",
        backgroundColor = Color(0xFF1877F2),
        contentColor = Color.White,
        icon = R.drawable.fui_ic_facebook_white_22dp
    )),
    GITHUB(ProviderStyle(
        label = "GitHub",
        backgroundColor = Color(0xFF24292E),
        contentColor = Color.White,
        icon = R.drawable.fui_ic_github_white_24dp
    ));

    override fun toString(): String {
        return when (this) {
            GOOGLE -> "google.com"
            FACEBOOK -> "facebook.com"
            GITHUB -> "github.com"
        }
    }

    companion object {
        /**
         * Converts a string to a Provider
         *
         * @param str the provider as a string
         * @return the corresponding Provider, or `null` if no providers match
         */
        fun valueOfOrNull(str: String): Provider? {
            return entries.firstOrNull { it.toString() == str }
        }
    }
}
