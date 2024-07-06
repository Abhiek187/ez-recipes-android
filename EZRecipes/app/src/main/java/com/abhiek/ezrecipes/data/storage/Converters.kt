package com.abhiek.ezrecipes.data.storage

import android.util.Log
import androidx.room.TypeConverter
import com.abhiek.ezrecipes.data.models.Recipe
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class Converters {
    val gson = Gson()

    companion object {
        private const val TAG = "Converters"
    }

    @TypeConverter
    fun fromRecipeString(recipeStr: String?): Recipe? {
        try {
            return gson.fromJson(recipeStr, Recipe::class.java)
        } catch (error: JsonSyntaxException) {
            Log.w(TAG, "Failed to parse recipe string: $recipeStr")
            return null
        }
    }

    @TypeConverter
    fun recipeToString(recipe: Recipe?): String? {
        return gson.toJson(recipe)
    }
}
