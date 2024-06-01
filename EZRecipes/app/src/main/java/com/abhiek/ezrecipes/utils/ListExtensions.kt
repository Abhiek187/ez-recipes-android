package com.abhiek.ezrecipes.utils

/**
 * Compares if two lists of similar type are equal.
 *
 * @param listOf The list to compare with
 * @return true if the lists are equal, false otherwise
 */
infix fun <E> List<E>.contentEquals(listOf: List<E>): Boolean {
    if (this.size != listOf.size) return false

    for (i in this.indices) {
        if (this[i] != listOf[i]) return false
    }

    return true
}
