package me.znotchill.honeydew.host.backend.config.model

data class AppConfig(
    val server: ServerConfig,
    val api: ApiConfig,
    val database: DatabaseConfig,
    val redis: RedisConfig,
)