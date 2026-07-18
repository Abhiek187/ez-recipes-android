package com.abhiek.ezrecipes.data.serializers

import android.util.Log
import com.abhiek.ezrecipes.data.recipe.MealType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MealTypeSerializer: KSerializer<MealType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.abhiek.ezrecipes.recipe.models.mealtype",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: MealType) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): MealType {
        val rawStringValue = decoder.decodeString()
        val stringValue = if (rawStringValue == "hor d'oeuvre") {
            "HOR_D_OEUVRE"
        } else {
            rawStringValue.replace(" ", "_").uppercase()
        }

        return try {
            MealType.valueOf(stringValue)
        } catch (_: IllegalArgumentException) {
            Log.w("MealTypeSerializer", "Encountered an unknown meal type: $stringValue")
            MealType.UNKNOWN
        }
    }
}
