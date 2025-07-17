package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable
import me.znotchill.honeydew.common.database.model.PlayerModel

@Serializable
data class UpdatePlayersRequest(
    val key: String,
    val players: List<PlayerModel>
)

@Serializable
data class UpdatePlayersResponse(
    val success: Boolean
)