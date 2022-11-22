package com.abhiek.ezrecipes.ui.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light Mode",
    group = "Displays",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = true
)
@Preview(
    name = "Dark Mode",
    group = "Displays",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true
)
annotation class DisplayPreviews
