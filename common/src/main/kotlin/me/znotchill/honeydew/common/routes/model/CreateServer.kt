package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateServerRequest(
    val name: String,
    val description: String,
    val ip: String,
    val port: Int
)

@Serializable
data class CreateServerResponse(
    val id: String,
    val key: String,
    val success: Boolean
)