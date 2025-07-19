package me.znotchill.honeydew.host.backend.redis.interfaces

import io.lettuce.core.api.sync.RedisCommands

interface RedisBacked {
    val redis: RedisCommands<String, String>
    val prefix: String
}