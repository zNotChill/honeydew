package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateLogRequest(
    val key: String,
    val channelId: String,
    val message: String,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class CreateLogResponse(
    val success: Boolean,
    val messageId: String,
)