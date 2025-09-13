package com.abhiek.ezrecipes.data.models

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
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
    var rating: Int? = null,
    var spiceLevel: List<SpiceLevel> = listOf(),
    var type: List<MealType> = listOf(),
    var culture: List<Cuisine> = listOf(),
    var token: String? = null, // either an ObjectId or searchSequenceToken for pagination
    var sort: RecipeSortField? = null,
    var asc: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        // Filter all the keys that aren't defined separately in the service
        val omittedKeys = listOf("vegetarian", "vegan", "gluten-free", "healthy", "cheap",
            "sustainable", "spice-level", "type", "culture", "asc")
        val gson = GsonBuilder()
            // Convert all the keys to kebab-case
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            // Convert whole numbers to longs instead of doubles
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create()
        val json = gson.toJson(this)
        val map = gson.fromJson<Map<String, Any>>(
            json, object: TypeToken<Map<String, Any>>() {}.type
        )
        return map.filter { (key, _) -> !omittedKeys.contains(key) }
    }
}
