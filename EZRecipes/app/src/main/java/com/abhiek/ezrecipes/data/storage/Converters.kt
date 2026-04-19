package com.abhiek.ezrecipes.data.storage

import android.util.Log
import androidx.room.TypeConverter
import com.abhiek.ezrecipes.data.recipe.Recipe
import kotlinx.serialization.json.Json

class Converters {
    companion object {
        private const val TAG = "Converters"
    }

    @TypeConverter
    fun fromRecipeString(recipeStr: String): Recipe? {
        try {
            return Json.decodeFromString<Recipe>(recipeStr)
        } catch (error: Exception) {
            Log.w(TAG, "Failed to parse recipe string: $recipeStr")
            Log.w(TAG, "Error: ${error.localizedMessage}")
            return null
        }
    }

    @TypeConverter
    fun recipeToString(recipe: Recipe): String {
        return Json.encodeToString(recipe)
    }
}
