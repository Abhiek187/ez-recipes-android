package com.abhiek.ezrecipes.utils

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
