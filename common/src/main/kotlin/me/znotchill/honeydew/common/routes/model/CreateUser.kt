package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val id: String,
    val username: String,
    val avatar: String?,
    val accessToken: String
)

@Serializable
data class CreateUserResponse(
    val success: Boolean,
    val accessToken: String,
    val userId: String
)