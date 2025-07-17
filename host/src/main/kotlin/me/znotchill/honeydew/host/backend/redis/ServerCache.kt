package me.znotchill.honeydew.host.backend.redis

import io.lettuce.core.api.sync.RedisCommands
import kotlinx.serialization.builtins.ListSerializer
import me.znotchill.honeydew.common.database.model.LogChannelModel
import me.znotchill.honeydew.common.database.model.LogModel
import me.znotchill.honeydew.common.database.model.PlayerModel
import me.znotchill.honeydew.common.database.model.StatusModel
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getServer
import me.znotchill.honeydew.host.backend.database.tables.Servers
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ServerCacheStore {
    private val cacheMap = ConcurrentHashMap<String, ServerCache>()

    fun get(id: String): ServerCache? = cacheMap[id]
    fun getAll() = cacheMap

    fun getOrCreate(id: String, redis: RedisCommands<String, String>): ServerCache {
        return cacheMap.computeIfAbsent(id) {
            val server = ServerCache(id, redis)
            server.populate()
            server
        }
    }
}

class ServerCache(
    val id: String,
    val redis: RedisCommands<String, String>
) {
    val prefix = "server:$id"

    var name: String by redisString("name")
    var description: String by redisString("description")
    var ip: String by redisString("ip")
    var key: String by redisString("key")

    var port: Int by redisInt("port")
    var lastPing: Int by redisInt("last_ping")

    var status: StatusModel by redisJson("status", StatusModel.serializer())
    var players: List<PlayerModel> by redisJson(
        "players",
        ListSerializer(PlayerModel.serializer())
    )

    var logs: List<LogModel> by redisJson(
        "logs",
        ListSerializer(LogModel.serializer())
    )

    var logChannels: List<LogChannelModel> by redisJson(
        "logChannels",
        ListSerializer(LogChannelModel.serializer())
    )

    fun appendLog(log: LogModel) {
        logs = logs + log
    }

    fun appendLogChannel(logChannel: LogChannelModel) {
        logChannels = logChannels + logChannel
    }

    fun populate() {
        val server = getServer(UUID.fromString(id)) ?: return

        name = server.name
        description = server.description
        ip = server.ip
        port = server.port
        key = server.key
        lastPing = server.lastPing
        status = server.status
        players = server.players
        logs = server.logs
        logChannels = server.logChannels
    }

    fun flushToDatabase() {
        val uuid = UUID.fromString(id)
        getServer(uuid) ?: return

        transaction {
            Servers.update({ Servers.id eq uuid }) {
                it[name] = this@ServerCache.name
                it[description] = this@ServerCache.description
                it[ip] = this@ServerCache.ip
                it[port] = this@ServerCache.port
                it[key] = this@ServerCache.key
                it[lastPing] = this@ServerCache.lastPing
                it[status] = this@ServerCache.status
                it[players] = this@ServerCache.players
                it[logs] = this@ServerCache.logs
                it[logChannels] = this@ServerCache.logChannels
            }
        }
    }
}