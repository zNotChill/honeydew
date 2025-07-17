package me.znotchill.honeydew.host.backend.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import me.znotchill.honeydew.host.backend.config.ConfigManager

object RedisManager {
    private var connection: StatefulRedisConnection<String, String>? = null
    val commands: RedisCommands<String, String>
        get() = connection?.sync() ?: error("Redis not initialized!")

    fun init() {
        val client = RedisClient.create(
            ConfigManager.appConfig.redis.url
        )
        connection = client.connect()
    }
}