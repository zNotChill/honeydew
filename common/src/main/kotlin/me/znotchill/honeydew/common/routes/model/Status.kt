package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    val version: String,
)