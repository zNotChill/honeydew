package me.znotchill.honeydew.host.backend.config.model

data class DatabaseConfig(
    val url: String,
    val driver: String,
    val username: String,
    val password: String
)