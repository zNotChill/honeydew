package me.znotchill.honeydew.common

import me.znotchill.honeydew.common.model.PermissionModel
import me.znotchill.honeydew.common.database.model.UserRoleModel

object PermissionManager {
    val rolePermissions: Map<UserRoleModel, Set<PermissionModel>> = mapOf(
        UserRoleModel.OWNER to PermissionModel.entries.toSet(),

        UserRoleModel.ADMIN to setOf(
            PermissionModel.START_SERVER,
            PermissionModel.STOP_SERVER,
            PermissionModel.EDIT_SERVER,
            PermissionModel.ACCESS_CONSOLE,
            PermissionModel.VIEW_LOGS,
            PermissionModel.MANAGE_USERS,
            PermissionModel.ASSIGN_ROLES,
            PermissionModel.EXECUTE_COMMANDS,
            PermissionModel.MANAGE_LOG_CHANNELS,
            PermissionModel.VIEW_STATS,
        ),

        UserRoleModel.MODERATOR to setOf(
            PermissionModel.KICK_PLAYER,
            PermissionModel.BAN_PLAYER,
            PermissionModel.VIEW_LOGS
        ),

        UserRoleModel.SUPPORT to setOf(
            PermissionModel.VIEW_LOGS,
            PermissionModel.VIEW_STATS
        ),
    )

    fun hasPermission(role: UserRoleModel, permission: PermissionModel): Boolean {
        return rolePermissions[role]?.contains(permission) == true
    }
}