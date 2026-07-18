package com.abhiek.ezrecipes.data.serializers

import android.util.Log
import com.abhiek.ezrecipes.data.recipe.Cuisine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CuisineSerializer: KSerializer<Cuisine> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.abhiek.ezrecipes.recipe.models.cuisine",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Cuisine) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Cuisine {
        val stringValue = decoder.decodeString().replace(" ", "_").uppercase()

        return try {
            Cuisine.valueOf(stringValue)
        } catch (_: IllegalArgumentException) {
            Log.w("CuisineSerializer", "Encountered an unknown cuisine: $stringValue")
            Cuisine.UNKNOWN
        }
    }
}
