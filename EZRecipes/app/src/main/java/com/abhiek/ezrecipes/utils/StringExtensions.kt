package com.abhiek.ezrecipes.utils

import android.os.Build
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

/**
 * Capitalizes the first letter of each word in a string.
 *
 * For example, "the quick brown fox" --> "The Quick Brown Fox"
 *
 * @return a new string with each word capitalized
 */
fun String.capitalizeWords(): String {
    // Split the string by spaces to get each word
    return split(" ")
        .joinToString(" ") { word ->
            // When joining each word together, replace the first character with its uppercase form
            word.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(
                    Locale.getDefault()
                ) else char.toString()
            }
        }
}

/**
 * Creates an annotated string with a portion of it bold
 *
 * @param text the string to annotate
 * @param startIndex the index to start bolding, inclusive
 * @param endIndex the index to end bolding, exclusive, defaults to the end of the string
 * @return the annotated string
 */
fun boldAnnotatedString(
    text: String,
    startIndex: Int = 0,
    endIndex: Int? = null
) = buildAnnotatedString {
    append(text)

    // Bold only the portion: [startIndex, endIndex)
    addStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Bold
        ),
        start = startIndex,
        end = endIndex ?: text.length
    )
}

/**
 * Converts an ISO 8601 date string to a Unix timestamp
 *
 * @return the Unix timestamp in milliseconds, or null if the string is invalid
 */
fun String.toUnixTimestamp(): Long? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(this).toEpochMilli()
        } else {
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = dateFormat.parse(this)
            date?.time
        }
    } catch (_: Exception) {
        null
    }
}
