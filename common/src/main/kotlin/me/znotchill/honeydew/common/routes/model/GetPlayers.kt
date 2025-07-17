package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable
import me.znotchill.honeydew.common.database.model.PlayerModel

@Serializable
data class GetPlayersResponse(
    val success: Boolean,
    val players: List<PlayerModel>
)