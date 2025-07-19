package me.znotchill.honeydew.common.database.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRoleModel {
    OWNER,
    ADMIN,
    MODERATOR,
    SUPPORT,
}