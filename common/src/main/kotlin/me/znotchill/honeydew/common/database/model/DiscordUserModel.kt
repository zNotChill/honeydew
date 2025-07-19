package me.znotchill.honeydew.common.database.model

import kotlinx.serialization.Serializable

@Serializable
data class DiscordUserModel(
    val id: String,
    val username: String,
    val discriminator: String,
    val avatar: String?
)