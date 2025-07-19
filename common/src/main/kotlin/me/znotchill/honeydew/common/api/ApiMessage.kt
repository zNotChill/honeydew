package me.znotchill.honeydew.common.api

enum class ApiMessage(
    val status: String,
    val type: String,
    val message: String,
    val success: Boolean = true,
) {
    REPORT_RECEIVED(
        "ok",
        "REPORT_RECEIVED",
        "Report received successfully."
    ),
    INVALID_SERVER_KEY(
        "error",
        "INVALID_SERVER_KEY",
        "Invalid server key.",
        false
    ),
    INVALID_SERVER_ID(
        "error",
        "INVALID_SERVER_ID",
        "Invalid server id.",
        false
    ),
    MISSING_FIELDS(
        "error",
        "MISSING_FIELDS",
        "Required fields are missing.",
        false
    ),
    SERVER_ONLINE(
        "ok",
        "SERVER_ONLINE",
        "Server is online."
    ),
    SERVER_OFFLINE(
        "error",
        "SERVER_OFFLINE",
        "Server is offline."
    ),
    INVALID_SERVER_NAME(
        "error",
        "INVALID_SERVER_NAME",
        "Server name should be within 3-30 characters.",
        false
    ),
    INVALID_SERVER_DESCRIPTION(
        "error",
        "INVALID_SERVER_DESCRIPTION",
        "Server description should be no more than 60 characters.",
        false
    ),
    SERVER_ALREADY_EXISTS(
        "error",
        "SERVER_ALREADY_EXISTS",
        "An identical server already exists.",
        false
    ),
    SERVER_DOES_NOT_EXIST(
        "error",
        "SERVER_DOES_NOT_EXIST",
        "A server with this name does not exist.",
        false
    ),
    INVALID_CREDENTIALS(
        "error",
        "INVALID_CREDENTIALS",
        "Invalid key or token.",
        false
    ),
    ERROR(
        "error",
        "ERROR",
        "An unexpected error occurred.",
        false
    ),
    LOG_CHANNEL_ALREADY_EXISTS(
        "error",
        "CHANNEL_ALREADY_EXISTS",
        "An identical log channel already exists.",
        false
    ),
    MISSING_CODE(
        "error",
        "MISSING_CODE",
        "Missing code.",
        false
    ),
    FAILED_TO_GET_TOKEN(
        "error",
        "FAILED_TO_GET_TOKEN",
        "Failed to get token.",
        false
    ),
    FAILED_TO_GET_USER(
        "error",
        "FAILED_TO_GET_USER",
        "Failed to get user info.",
        false
    )
}