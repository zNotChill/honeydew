package me.znotchill.honeydew.common.database.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationModel(
    val worldUuid: String,
    val worldName: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val pitch: Double,
    val yaw: Double
)