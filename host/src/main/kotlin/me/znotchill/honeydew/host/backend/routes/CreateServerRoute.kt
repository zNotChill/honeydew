package me.znotchill.honeydew.host.backend.routes

import io.ktor.http.HttpStatusCode
import me.znotchill.honeydew.common.api.ApiMessage
import me.znotchill.honeydew.common.api.ApiResponse
import me.znotchill.honeydew.host.backend.interfaces.RouteModule
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.znotchill.honeydew.common.database.model.LogModel
import me.znotchill.honeydew.host.backend.database.DatabaseManager
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getServer
import me.znotchill.honeydew.common.routes.model.CreateServerRequest
import me.znotchill.honeydew.host.backend.config.ConfigManager
import me.znotchill.honeydew.host.backend.utils.getHumanTimestamp

object CreateServerRoute : RouteModule() {
    override fun register(application: Application) {
        application.routing {
            post("/api/v1/servers/create") {
                val request = call.receive<CreateServerRequest>()

                val adminKey = call.request.headers["X-Admin-Key"]
                if (adminKey != ConfigManager.appConfig.api.key) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.from(
                            ApiMessage.INVALID_CREDENTIALS, null
                        )
                    )
                    return@post
                }

                if (request.name.length < 3 || request.name.length > 30) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(
                            ApiMessage.INVALID_SERVER_NAME, null
                        )
                    )
                    return@post
                }
                if (request.description.length > 60) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(
                            ApiMessage.INVALID_SERVER_DESCRIPTION, null
                        )
                    )
                    return@post
                }

                if (getServer(request.name) != null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(
                            ApiMessage.SERVER_ALREADY_EXISTS, null
                        )
                    )
                    return@post
                }

                val response = DatabaseManager.createServer(request)
                val server = getServer(request.name)

                if (server == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(
                            ApiMessage.ERROR, null
                        )
                    )
                    return@post
                }

                call.respond(
                    HttpStatusCode.Created,
                    response
                )
            }
        }
    }
}

