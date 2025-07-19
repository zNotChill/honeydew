package me.znotchill.honeydew.common.database.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserStatusModel {
    ONLINE,
    IDLE,
    OFFLINE
}