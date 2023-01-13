package com.abhiek.ezrecipes.ui.previews

import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "phone",
    group = "Devices",
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Preview(
    name = "foldable",
    group = "Devices",
    device = "spec:width=673.5dp,height=841dp,dpi=480",
    showSystemUi = true
)
@Preview(
    name = "tablet",
    group = "Devices",
    device = "spec:width=1280dp,height=800dp,dpi=480",
    showSystemUi = true
)
@Preview(
    name = "desktop",
    group = "Devices",
    device = "spec:width=1920dp,height=1080dp,dpi=480",
    showSystemUi = true
)
annotation class DevicePreviews
