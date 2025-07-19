package me.znotchill.honeydew.host.backend.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import me.znotchill.honeydew.host.backend.config.model.ApiConfig
import me.znotchill.honeydew.host.backend.config.model.AppConfig
import me.znotchill.honeydew.host.backend.config.model.DatabaseConfig
import me.znotchill.honeydew.host.backend.config.model.DiscordConfig
import me.znotchill.honeydew.host.backend.config.model.RedisConfig
import me.znotchill.honeydew.host.backend.config.model.ServerConfig

object ConfigManager {
    private val config: Config = ConfigFactory.load()

    val appConfig: AppConfig by lazy {
        val serverConfig = ServerConfig(
            host = config.getString("server.host"),
            port = config.getInt("server.port")
        )
        val apiConfig = ApiConfig(
            key = config.getString("api.key"),
            endpoint = config.getString("api.endpoint")
        )
        val databaseConfig = DatabaseConfig(
            url = config.getString("database.url"),
            username = config.getString("database.username"),
            password = config.getString("database.password"),
            driver = config.getString("database.driver"),
        )
        val redisConfig = RedisConfig(
            url = config.getString("redis.url"),
        )
        val discordConfig = DiscordConfig(
            clientId = config.getString("discord.clientId"),
            clientSecret = config.getString("discord.clientSecret"),
        )
        AppConfig(serverConfig, apiConfig, databaseConfig, redisConfig, discordConfig)
    }
}