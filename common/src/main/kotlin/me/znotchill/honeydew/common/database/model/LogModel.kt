package me.znotchill.honeydew.common.database.model

import kotlinx.serialization.Serializable

@Serializable
data class LogModel(
    val id: String,
    val channelId: String,
    val message: String,
    val metadata: Map<String, String> = emptyMap(),
    val timestamp: String
)