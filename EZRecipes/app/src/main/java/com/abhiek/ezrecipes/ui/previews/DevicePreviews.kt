package com.abhiek.ezrecipes.ui.previews

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Small Screen",
    group = "Devices",
    device = Devices.NEXUS_5,
    showSystemUi = true
)
@Preview(
    name = "Medium Screen",
    group = "Devices",
    device = Devices.PIXEL_3,
    showSystemUi = true
)
@Preview(
    name = "Large Screen",
    group = "Devices",
    device = Devices.TABLET,
    showSystemUi = true
)
annotation class DevicePreviews
