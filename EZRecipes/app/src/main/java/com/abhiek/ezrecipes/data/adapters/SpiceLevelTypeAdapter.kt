package com.abhiek.ezrecipes.data.adapters

import android.util.Log
import com.abhiek.ezrecipes.data.models.SpiceLevel
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.IllegalArgumentException

class SpiceLevelTypeAdapter: TypeAdapter<SpiceLevel>() {
    override fun write(writer: JsonWriter?, value: SpiceLevel?) {
        // Encode spice levels using the same toString() method
        writer?.value(value?.toString())
    }

    override fun read(reader: JsonReader?): SpiceLevel {
        // Reverse the logic of toString() and determine if the value matches any enum value
        val stringValue = reader?.nextString()?.uppercase()

        return try {
            SpiceLevel.valueOf(stringValue ?: "")
        } catch (_: IllegalArgumentException) {
            // Default to unknown if spoonacular returns a value that's undocumented
            Log.w("SpiceLevelTypeAdapter", "Encountered an unknown spice level: $stringValue")
            SpiceLevel.UNKNOWN
        }
    }
}
