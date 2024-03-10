package com.abhiek.ezrecipes.data.models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class RecipeFilter(
    var query: String = "",
    var minCals: Int? = null,
    var maxCals: Int? = null,
    var vegetarian: Boolean = false,
    var vegan: Boolean = false,
    var glutenFree: Boolean = false,
    var healthy: Boolean = false,
    var cheap: Boolean = false,
    var sustainable: Boolean = false,
    var spiceLevel: List<SpiceLevel> = listOf(),
    var type: List<MealType> = listOf(),
    var culture: List<Cuisine> = listOf()
) {
    fun toMap(): Map<String, Any> {
        // Filter all the keys that aren't defined separately in the service
        val omittedKeys = listOf("vegetarian", "vegan", "glutenFree", "healthy", "cheap",
            "sustainable", "spiceLevel", "type", "culture")
        val gson = Gson()
        val json = gson.toJson(this)
        val map = gson.fromJson<Map<String, Any>>(json, object: TypeToken<Map<String, Any>>() {}.type)
        return map.filter { (key, _) -> !omittedKeys.contains(key) }
    }
}
