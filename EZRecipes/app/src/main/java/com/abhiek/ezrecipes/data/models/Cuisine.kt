package com.abhiek.ezrecipes.data.models

import com.abhiek.ezrecipes.utils.capitalizeWords

enum class Cuisine {
    AFRICAN,
    ASIAN,
    AMERICAN,
    BRITISH,
    CAJUN,
    CARIBBEAN,
    CHINESE,
    EASTERN_EUROPEAN,
    EUROPEAN,
    FRENCH,
    GERMAN,
    GREEK,
    INDIAN,
    IRISH,
    ITALIAN,
    JAPANESE,
    JEWISH,
    KOREAN,
    LATIN_AMERICAN,
    MEDITERRANEAN,
    MEXICAN,
    MIDDLE_EASTERN,
    NORDIC,
    SOUTHERN,
    SPANISH,
    THAI,
    VIETNAMESE,
    ENGLISH,
    SCOTTISH,
    SOUTH_AMERICAN,
    CREOLE,
    UNKNOWN;

    override fun toString() = name.replace("_", " ").lowercase().capitalizeWords()
}
