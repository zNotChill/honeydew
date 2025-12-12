package me.znotchill.honeydew.host.backend.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.znotchill.honeydew.common.api.ApiMessage
import me.znotchill.honeydew.common.api.ApiResponse
import me.znotchill.honeydew.common.database.model.LogChannelModel
import me.znotchill.honeydew.common.database.model.LogScope
import me.znotchill.honeydew.common.routes.model.CreateLogChannelResponse
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getLogChannelByName
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getServer
import me.znotchill.honeydew.host.backend.interfaces.RouteModule
import me.znotchill.honeydew.host.backend.routes.validators.createLogChannelValidator
import java.util.*

object CreateLogChannelRoute : RouteModule() {
    override fun register(application: Application) {
        application.routing {
            post("/api/v1/servers/{id}/logChannels") {
                val id = call.parameters["id"]

                call.requireAdminKey()

                val request = call.parseAndValidate(createLogChannelValidator)
                if (request == null) return@post

                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.MISSING_FIELDS, null)
                    )
                    return@post
                }

                if (id.length != 36) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.INVALID_SERVER_ID, null)
                    )
                    return@post
                }

                val serverId = try {
                    UUID.fromString(id)
                } catch (_: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.ERROR, null)
                    )
                    return@post
                }

                val server = getServer(serverId, request.key)
                if (server == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.from(ApiMessage.SERVER_DOES_NOT_EXIST, null)
                    )
                    return@post
                }

                val channel = getLogChannelByName(serverId, request.name)
                if (channel != null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.from(ApiMessage.LOG_CHANNEL_ALREADY_EXISTS, null)
                    )
                    return@post
                }

                val logChannelModel = LogChannelModel(
                    id = UUID.randomUUID().toString(),
                    name = request.name,
                    description = request.description,
                    scope = LogScope.SERVER
                )

                server.cache.appendLogChannel(logChannelModel)

                call.respond(
                    HttpStatusCode.OK,
                    CreateLogChannelResponse(
                        success = true,
                        channelId = logChannelModel.id
                    )
                )
            }
        }
    }
}

