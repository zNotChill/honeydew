package me.znotchill.honeydew.host.backend.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.znotchill.honeydew.common.api.ApiMessage
import me.znotchill.honeydew.common.api.ApiResponse
import me.znotchill.honeydew.common.routes.model.GetLogsResponse
import me.znotchill.honeydew.host.backend.config.ConfigManager
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getServer
import me.znotchill.honeydew.host.backend.interfaces.RouteModule
import java.util.*

object GetLogsRoute : RouteModule {
    override fun register(application: Application) {
        application.routing {
            get("/api/servers/{id}/logs") {
                val id = call.parameters["id"]

                val adminKey = call.request.headers["X-Admin-Key"]
                if (adminKey != ConfigManager.appConfig.api.key) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.from(
                            ApiMessage.INVALID_CREDENTIALS, null
                        )
                    )
                    return@get
                }

                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.MISSING_FIELDS, null)
                    )
                    return@get
                }

                if (id.length != 36) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.INVALID_SERVER_ID, null)
                    )
                    return@get
                }

                val serverId = try {
                    UUID.fromString(id)
                } catch (_: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.ERROR, null)
                    )
                    return@get
                }

                val server = getServer(serverId)
                if (server == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.from(ApiMessage.SERVER_DOES_NOT_EXIST, null)
                    )
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    GetLogsResponse(
                        success = true,
                        logs = server.cache.logs
                    )
                )
            }
        }
    }
}

