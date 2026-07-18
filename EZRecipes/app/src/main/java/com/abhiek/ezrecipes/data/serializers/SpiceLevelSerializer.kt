package com.abhiek.ezrecipes.data.serializers

import android.util.Log
import com.abhiek.ezrecipes.data.recipe.SpiceLevel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SpiceLevelSerializer: KSerializer<SpiceLevel> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.abhiek.ezrecipes.recipe.models.spicelevel",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: SpiceLevel) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): SpiceLevel {
        // Reverse the logic of toString() and determine if the value matches any enum value
        val stringValue = decoder.decodeString().uppercase()

        return try {
            SpiceLevel.valueOf(stringValue)
        } catch (_: IllegalArgumentException) {
            // Default to unknown if spoonacular returns a value that's undocumented
            Log.w("SpiceLevelSerializer", "Encountered an unknown spice level: $stringValue")
            SpiceLevel.UNKNOWN
        }
    }
}
