package me.znotchill.honeydew.host.backend.database

import me.znotchill.honeydew.common.database.model.LogChannelModel
import me.znotchill.honeydew.host.backend.config.ConfigManager
import me.znotchill.honeydew.common.database.model.PlayerModel
import me.znotchill.honeydew.common.database.model.StatusModel
import me.znotchill.honeydew.host.backend.database.tables.Server
import me.znotchill.honeydew.host.backend.database.tables.Servers
import me.znotchill.honeydew.common.routes.model.CreateServerRequest
import me.znotchill.honeydew.common.routes.model.CreateServerResponse
import me.znotchill.honeydew.common.routes.model.UpdatePlayersResponse
import me.znotchill.honeydew.host.backend.utils.generateSecureToken
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

object DatabaseManager {
    val cfg = ConfigManager.appConfig

    fun init() {
        Database.connect(
            cfg.database.url,
            driver = cfg.database.driver,
            user = cfg.database.username,
            password = cfg.database.password
        )

        transaction {
            SchemaUtils.create(Servers)
        }
    }

    fun createServer(
        request: CreateServerRequest
    ): CreateServerResponse {
        val server = transaction {
            val token = generateSecureToken()

            val newServer = Server.new {
                name = request.name
                description = request.description
                players = emptyList()
                status = StatusModel.OFFLINE
                lastPing = 0
                ip = request.ip
                port = request.port
                key = token
            }

            newServer to token
        }

        return CreateServerResponse(
            id = server.first.id.value.toString(),
            key = server.second,
            success = true,
        )
    }

    fun setPlayers(serverId: UUID, key: String, players: List<PlayerModel>): UpdatePlayersResponse {
        return transaction {
            val server = Server.find {
                (Servers.id eq serverId) and (Servers.key eq key)
            }.firstOrNull() ?: return@transaction UpdatePlayersResponse(
                success = false
            )

            server.players = players

            UpdatePlayersResponse(
                success = true
            )
        }
    }

    fun getServer(uuid: UUID): Server? {
        return transaction {
            Server.find { Servers.id eq uuid }.firstOrNull()
        }
    }

    fun getServer(name: String): Server? {
        return transaction {
            Server.find {
                Servers.name.lowerCase() eq name.lowercase()
            }.firstOrNull()
        }
    }

    fun getServer(uuid: UUID, key: String): Server? {
        return transaction {
            Server.find {
                (Servers.id eq uuid) and
                (Servers.key eq key)
            }.firstOrNull()
        }
    }

    fun getLogChannelById(serverId: UUID, channelId: String): LogChannelModel? =
        getServerLogChannels(serverId)?.find { it.id == channelId }

    fun getLogChannelByName(serverId: UUID, name: String): LogChannelModel? =
        getServerLogChannels(serverId)?.find { it.name == name }

    private fun getServerLogChannels(serverId: UUID): List<LogChannelModel>? {
        return transaction {
            Server.findById(serverId)?.logChannels
        }
    }
}