package com.abhiek.ezrecipes.ui.previews

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Portrait",
    group = "Orientations",
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Preview(
    name = "Landscape",
    group = "Orientations",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 1024,
    heightDp = 632,
    showSystemUi = true
)
annotation class OrientationPreviews
