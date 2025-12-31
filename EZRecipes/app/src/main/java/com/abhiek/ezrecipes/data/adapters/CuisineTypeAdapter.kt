package com.abhiek.ezrecipes.data.adapters

import android.util.Log
import com.abhiek.ezrecipes.data.models.Cuisine
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.IllegalArgumentException

class CuisineTypeAdapter: TypeAdapter<Cuisine>() {
    override fun write(writer: JsonWriter?, value: Cuisine?) {
        writer?.value(value?.toString())
    }

    override fun read(reader: JsonReader?): Cuisine {
        val stringValue = reader?.nextString()?.replace(" ", "_")?.uppercase()

        return try {
            Cuisine.valueOf(stringValue ?: "")
        } catch (_: IllegalArgumentException) {
            Log.w("CuisineTypeAdapter", "Encountered an unknown cuisine: $stringValue")
            Cuisine.UNKNOWN
        }
    }
}
