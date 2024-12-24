package com.abhiek.ezrecipes.utils

import android.content.res.Resources
import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.text.CompactDecimalFormat
import android.os.Build
import android.util.TypedValue
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale

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

/**
 * Converts an integer to a shorthand string. (e.g. 1234 -> 1.2K)
 *
 * @return the int in string format
 */
fun Int.toShorthand(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        NumberFormatter.with()
            .locale(Locale.getDefault())
            .notation(Notation.compactShort())
            .format(this)
            .toString()
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        CompactDecimalFormat.getInstance(
            Locale.getDefault(),
            CompactDecimalFormat.CompactStyle.SHORT
        ).format(this)
    } else {
        // # = digit if non-zero, blank if 0
        val formatter = DecimalFormat("#.#")
        formatter.roundingMode = RoundingMode.DOWN
        when {
            this < 1e3 -> this.toString()
            this < 1e6 -> formatter.format(this / 1e3) + "K"
            this < 1e9 -> formatter.format(this / 1e6) + "M"
            else -> formatter.format(this / 1e9) + "B"
        }
    }
}
