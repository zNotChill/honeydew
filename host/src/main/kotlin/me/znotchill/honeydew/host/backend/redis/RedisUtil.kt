package me.znotchill.honeydew.host.backend.redis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import me.znotchill.honeydew.host.backend.redis.interfaces.RedisBacked
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun redisString(field: String): ReadWriteProperty<RedisBacked, String> =
    object : ReadWriteProperty<RedisBacked, String> {
        override fun getValue(thisRef: RedisBacked, property: KProperty<*>): String {
            return thisRef.redis.hget(thisRef.prefix, field) ?: ""
        }

        override fun setValue(thisRef: RedisBacked, property: KProperty<*>, value: String) {
            thisRef.redis.hset(thisRef.prefix, field, value)
        }
    }

fun redisInt(field: String): ReadWriteProperty<RedisBacked, Int> =
    object : ReadWriteProperty<RedisBacked, Int> {
        override fun getValue(thisRef: RedisBacked, property: KProperty<*>): Int {
            return thisRef.redis.hget(thisRef.prefix, field)?.toIntOrNull() ?: 0
        }

        override fun setValue(thisRef: RedisBacked, property: KProperty<*>, value: Int) {
            thisRef.redis.hset(thisRef.prefix, field, value.toString())
        }
    }

fun <T> redisJson(field: String, serializer: KSerializer<T>): ReadWriteProperty<RedisBacked, T> =
    object : ReadWriteProperty<RedisBacked, T> {
        val json = Json { ignoreUnknownKeys = true }

        override fun getValue(thisRef: RedisBacked, property: KProperty<*>): T {
            val raw = thisRef.redis.hget(thisRef.prefix, field) ?: error("Missing field: $field")
            return json.decodeFromString(serializer, raw)
        }

        override fun setValue(thisRef: RedisBacked, property: KProperty<*>, value: T) {
            thisRef.redis.hset(thisRef.prefix, field, json.encodeToString(serializer, value))
        }
    }