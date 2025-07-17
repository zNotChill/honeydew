package me.znotchill.honeydew.host.backend.redis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun redisString(field: String): ReadWriteProperty<ServerCache, String> =
    object : ReadWriteProperty<ServerCache, String> {
        override fun getValue(thisRef: ServerCache, property: KProperty<*>): String {
            return thisRef.redis.hget(thisRef.prefix, field) ?: ""
        }

        override fun setValue(thisRef: ServerCache, property: KProperty<*>, value: String) {
            thisRef.redis.hset(thisRef.prefix, field, value)
        }
    }

fun redisInt(field: String): ReadWriteProperty<ServerCache, Int> =
    object : ReadWriteProperty<ServerCache, Int> {
        override fun getValue(thisRef: ServerCache, property: KProperty<*>): Int {
            return thisRef.redis.hget(thisRef.prefix, field)?.toIntOrNull() ?: 0
        }

        override fun setValue(thisRef: ServerCache, property: KProperty<*>, value: Int) {
            thisRef.redis.hset(thisRef.prefix, field, value.toString())
        }
    }

fun <T> redisJson(field: String, serializer: KSerializer<T>): ReadWriteProperty<ServerCache, T> =
    object : ReadWriteProperty<ServerCache, T> {
        val json = Json { ignoreUnknownKeys = true }

        override fun getValue(thisRef: ServerCache, property: KProperty<*>): T {
            val raw = thisRef.redis.hget(thisRef.prefix, field)
            return json.decodeFromString(serializer, raw)
        }

        override fun setValue(thisRef: ServerCache, property: KProperty<*>, value: T) {
            thisRef.redis.hset(thisRef.prefix, field, json.encodeToString(serializer, value))
        }
    }
