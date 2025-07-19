package me.znotchill.honeydew.common.model

enum class PermissionModel {
    // Server control
    CREATE_SERVER,
    DELETE_SERVER,
    START_SERVER,
    STOP_SERVER,
    EDIT_SERVER,

    // Player management
    KICK_PLAYER,
    BAN_PLAYER,
    VIEW_PLAYERS,

    // Server management
    ACCESS_CONSOLE,
    EXECUTE_COMMANDS,
    VIEW_LOGS,
    MANAGE_LOG_CHANNELS,

    // Support
    VIEW_STATS,

    // User management
    MANAGE_USERS,
    ASSIGN_ROLES,
}