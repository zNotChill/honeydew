package me.znotchill.honeydew.host.backend.database.tables

import kotlinx.serialization.builtins.ListSerializer
import me.znotchill.honeydew.common.database.model.LogChannelModel
import me.znotchill.honeydew.common.database.model.LogModel
import me.znotchill.honeydew.host.backend.ApiManager
import me.znotchill.honeydew.common.database.model.PlayerModel
import me.znotchill.honeydew.common.database.model.StatusModel
import me.znotchill.honeydew.host.backend.redis.RedisManager
import me.znotchill.honeydew.host.backend.redis.ServerCache
import me.znotchill.honeydew.host.backend.redis.ServerCacheStore
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import org.jetbrains.exposed.v1.json.jsonb
import java.util.UUID

object Servers : UUIDTable("servers") {
    val name = varchar("name", 30)
    val description = varchar("description", 60)
    val players = jsonb<List<PlayerModel>>(
        "players",
        ApiManager.jsonSerializer,
        ListSerializer(PlayerModel.serializer())
    ).default(emptyList())

    val status = enumerationByName("status", length = 10, StatusModel::class)
    val lastPing = integer("last_ping")
    val ip = varchar("ip", 15)
    val port = integer("port")
    val key = varchar("key", 255)

    val logs = jsonb<List<LogModel>>(
        "logs",
        ApiManager.jsonSerializer,
        ListSerializer(LogModel.serializer())
    ).default(emptyList())

    val logChannels = jsonb<List<LogChannelModel>>(
        "log_channels",
        ApiManager.jsonSerializer,
        ListSerializer(LogChannelModel.serializer())
    ).default(emptyList())
}

class Server(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Server>(Servers)

    var name by Servers.name
    var description by Servers.description
    var players by Servers.players
    var status by Servers.status
    var lastPing by Servers.lastPing
    var ip by Servers.ip
    var port by Servers.port
    var key by Servers.key
    var logs by Servers.logs
    var logChannels by Servers.logChannels

    val cache: ServerCache
        get() = ServerCacheStore.getOrCreate(this.id.value.toString(), RedisManager.commands)
}