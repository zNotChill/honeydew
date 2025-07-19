package me.znotchill.honeydew.host.backend.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class DiscordTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String
)