package me.znotchill.honeydew.client.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.znotchill.honeydew.common.database.model.LocationModel
import me.znotchill.honeydew.common.database.model.PlayerModel
import me.znotchill.honeydew.common.routes.model.UpdatePlayersRequest
import net.axay.kspigot.ipaddress.ipAddressOrNull
import org.bukkit.entity.Player as BukkitPlayer

object ApiManager {
    var id: String = ""
    var key: String = ""
    var url: String = ""
    var port: Int = 0
    var adminKey: String = ""

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun getFullUrl(): String {
        return "http://$url:$port"
    }

    fun setPlayersAsync(players: List<BukkitPlayer>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val structuredPlayers = players.map {
                    PlayerModel(
                        uuid = it.uniqueId.toString(),
                        username = it.name,
                        location = LocationModel(
                            worldUuid = it.location.world.uid.toString(),
                            worldName = it.location.world.name,
                            x = it.location.x,
                            y = it.location.y,
                            z = it.location.z,
                            pitch = it.location.pitch.toDouble(),
                            yaw = it.location.yaw.toDouble()
                        ),
                        brand = it.clientBrandName ?: "",
                        protocolVersion = it.protocolVersion,
                        protocolString = "",
                        locale = it.locale().toLanguageTag(),
                        health = it.health,
                        foodLevel = it.foodLevel,
                        gameMode = it.gameMode.ordinal,
                        ping = it.ping,
                        isOp = it.isOp,
                        level = it.level,
                        ip = it.ipAddressOrNull ?: ""
                    )
                }

                client.patch("${getFullUrl()}/api/v1/servers/$id/players") {
                    contentType(ContentType.Application.Json)
                    headers {
                        append("X-Admin-Key", adminKey)
                    }
                    setBody(UpdatePlayersRequest(
                        key = key,
                        players = structuredPlayers
                    ))
                }
            } catch (e: Exception) {
                println("Failed to send players: ${e.message}")
            }
        }
    }

}