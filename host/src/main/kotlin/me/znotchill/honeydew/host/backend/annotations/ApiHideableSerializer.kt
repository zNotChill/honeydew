package me.znotchill.honeydew.host.backend.annotations

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class ApiHideableSerializer<T>(
    private val originalSerializer: KSerializer<T>
) : KSerializer<T> {

    override val descriptor: SerialDescriptor = originalSerializer.descriptor

    override fun serialize(encoder: Encoder, value: T) {
        require(encoder is JsonEncoder)
        val jsonElement = encoder.json.encodeToJsonElement(originalSerializer, value)
        val jsonObject = jsonElement.jsonObject

        val filteredContent = buildMap {
            for (index in 0 until descriptor.elementsCount) {
                val name = descriptor.getElementName(index)
                val annotation = descriptor.getElementAnnotations(index).find { it is ApiHideable }
                if (annotation == null && name in jsonObject) {
                    put(name, jsonObject[name]!!)
                }
            }
        }

        encoder.encodeJsonElement(JsonObject(filteredContent))
    }

    override fun deserialize(decoder: Decoder): T {
        return originalSerializer.deserialize(decoder)
    }
}