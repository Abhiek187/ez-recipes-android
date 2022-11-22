package com.abhiek.ezrecipes.ui.previews

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Portrait",
    group = "Orientations",
    device = Devices.PHONE,
    showSystemUi = true
)
@Preview(
    name = "Landscape",
    group = "Orientations",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 1920,
    heightDp = 1080,
    showSystemUi = true
)
annotation class OrientationPreviews
