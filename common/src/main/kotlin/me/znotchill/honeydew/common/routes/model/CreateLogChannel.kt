package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateLogChannelRequest(
    val key: String,
    val name: String,
    val description: String
)

@Serializable
data class CreateLogChannelResponse(
    val success: Boolean,
    val channelId: String
)