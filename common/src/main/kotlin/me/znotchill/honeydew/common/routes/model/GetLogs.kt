package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable
import me.znotchill.honeydew.common.database.model.LogModel

@Serializable
data class GetLogsResponse(
    val success: Boolean,
    val logs: List<LogModel>
)