package me.znotchill.honeydew.host.backend.routes

import io.ktor.server.routing.patch
import me.znotchill.honeydew.common.routes.model.UpdatePlayersRequest
import java.util.UUID
import io.ktor.http.HttpStatusCode
import me.znotchill.honeydew.common.api.ApiMessage
import me.znotchill.honeydew.common.api.ApiResponse
import me.znotchill.honeydew.host.backend.interfaces.RouteModule
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import me.znotchill.honeydew.host.backend.database.DatabaseManager
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getServer
import me.znotchill.honeydew.common.routes.model.ValidationErrorDto
import me.znotchill.honeydew.host.backend.ApiManager
import me.znotchill.honeydew.host.backend.config.ConfigManager
import me.znotchill.honeydew.host.backend.routes.validators.updatePlayersValidator

object UpdatePlayersRoute : RouteModule {
    override fun register(application: Application) {
        application.routing {
            patch("/api/servers/{id}/players") {
                val id = call.parameters["id"]
                val request = call.receive<UpdatePlayersRequest>()

                val adminKey = call.request.headers["X-Admin-Key"]
                if (adminKey != ConfigManager.appConfig.api.key) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.from(
                            ApiMessage.INVALID_CREDENTIALS, null
                        )
                    )
                    return@patch
                }

                val validation = updatePlayersValidator.validate(request)
                if (!validation.errors.isEmpty()) {
                    val validationErrors = validation.errors.map {
                        ValidationErrorDto(
                            field = it.dataPath,
                            message = it.message
                        )
                    }

                    ApiManager.logger.info { "Validation failed for $id" }
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(
                            ApiMessage.ERROR,
                            details = validationErrors
                        )
                    )
                    return@patch
                }

                val serverId = try {
                    UUID.fromString(id)
                } catch (_: IllegalArgumentException) {
                    ApiManager.logger.info { "Invalid UUID provided for $id" }
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.ERROR, null)
                    )
                    return@patch
                }

                val server = getServer(serverId, request.key)
                if (server == null) {
                    ApiManager.logger.info { "Invalid server id/key provided for $serverId (${request.key})" }
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.from(ApiMessage.SERVER_DOES_NOT_EXIST, null)
                    )
                    return@patch
                }

                val players = DatabaseManager.setPlayers(serverId, request.key, request.players)

                ApiManager.logger.info { "Updating player list for ${server.name}" }
                call.respond(
                    HttpStatusCode.OK,
                    players
                )
            }
        }
    }
}

