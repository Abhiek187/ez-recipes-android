package com.abhiek.ezrecipes.data.adapters

import android.util.Log
import com.abhiek.ezrecipes.data.models.MealType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.IllegalArgumentException

class MealTypeAdapter: TypeAdapter<MealType>() {
    override fun write(writer: JsonWriter?, value: MealType?) {
        writer?.value(value?.toString())
    }

    override fun read(reader: JsonReader?): MealType {
        val rawStringValue = reader?.nextString()
        val stringValue = if (rawStringValue == "hor d'oeuvre") {
            "HOR_D_OEUVRE"
        } else {
            rawStringValue?.replace(" ", "_")?.uppercase()
        }

        return try {
            MealType.valueOf(stringValue ?: "")
        } catch (error: IllegalArgumentException) {
            Log.w("MealTypeAdapter", "Encountered an unknown meal type: $stringValue")
            MealType.UNKNOWN
        }
    }
}
