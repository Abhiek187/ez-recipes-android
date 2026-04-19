package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.serializers.MealTypeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MealTypeSerializer::class)
enum class MealType {
    MAIN_COURSE,
    SIDE_DISH,
    DESSERT,
    APPETIZER,
    SALAD,
    BREAD,
    BREAKFAST,
    SOUP,
    BEVERAGE,
    SAUCE,
    MARINADE,
    FINGERFOOD,
    SNACK,
    DRINK,
    ANTIPASTI,
    STARTER,
    ANTIPASTO,
    HOR_D_OEUVRE,
    LUNCH,
    MAIN_DISH,
    DINNER,
    MORNING_MEAL,
    BRUNCH,
    CONDIMENT,
    DIP,
    SPREAD,
    SMOOTHIE,
    COCKTAIL,
    MOCKTAIL,
    SEASONING,
    BATTER,
    UNKNOWN;

    override fun toString(): String {
        if (this == HOR_D_OEUVRE) return "hor d'oeuvre"

        return name.replace("_", " ").lowercase()
    }
}
