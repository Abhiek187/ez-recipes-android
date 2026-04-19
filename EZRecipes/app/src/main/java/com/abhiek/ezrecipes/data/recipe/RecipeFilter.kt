package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RecipeFilter(
    var query: String = "",
    @SerialName("min-cals")
    var minCals: Int? = null,
    @SerialName("max-cals")
    var maxCals: Int? = null,
    var vegetarian: Boolean = false,
    var vegan: Boolean = false,
    @SerialName("gluten-free")
    var glutenFree: Boolean = false,
    var healthy: Boolean = false,
    var cheap: Boolean = false,
    var sustainable: Boolean = false,
    var rating: Int? = null,
    @SerialName("spice-level")
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
        val json = Json.encodeToString(this)
        val map = Json.decodeFromString<Map<String, Any>>(json)
        return map.filter { (key, _) -> !omittedKeys.contains(key) }
            .entries.associate { (key, value) ->
                if (key == "sort") {
                    key to (value as String).replace("_", "-").lowercase()
                } else {
                    key to value
                }
            }
    }
}
