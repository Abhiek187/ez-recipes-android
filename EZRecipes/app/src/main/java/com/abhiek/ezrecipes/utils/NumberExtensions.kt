package com.abhiek.ezrecipes.utils

import android.content.res.Resources
import android.util.TypedValue

/**
 * Converts a measurement in dp to px using the device's display metrics.
 *
 * @return the value in px
 */
val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
)
