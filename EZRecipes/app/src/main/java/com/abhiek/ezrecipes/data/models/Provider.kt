package com.abhiek.ezrecipes.data.models

import androidx.compose.ui.graphics.Color

enum class Provider(val style: ProviderStyle) {
    // Source: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/src/main/res/values/styles.xml
    GOOGLE(ProviderStyle(
        label = "Google",
        backgroundColor = Color(0xFFFFFF),
        contentColor = Color(0x757575)
    )),
    FACEBOOK(ProviderStyle(
        label = "Facebook",
        backgroundColor = Color(0x1877F2),
        contentColor = Color(0xFFFFFF)
    )),
    GITHUB(ProviderStyle(
        label = "GitHub",
        backgroundColor = Color(0x24292E),
        contentColor = Color(0xFFFFFF)
    ));

    override fun toString(): String {
        return when (this) {
            GOOGLE -> "google.com"
            FACEBOOK -> "facebook.com"
            GITHUB -> "github.com"
        }
    }
}
