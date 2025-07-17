package me.znotchill.honeydew.common.database.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerModel(
    val uuid: String,
    val username: String,
    val location: LocationModel,
    val protocolVersion: Int,
    val protocolString: String,
    val brand: String,
    val locale: String,
    val health: Double,

    val foodLevel: Int,
    val gameMode: Int,
    val ping: Int,
    val isOp: Boolean,
    val level: Int,
    val ip: String,
)