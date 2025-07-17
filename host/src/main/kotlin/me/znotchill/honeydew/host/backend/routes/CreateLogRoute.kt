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
import me.znotchill.honeydew.common.routes.model.CreateLogRequest
import me.znotchill.honeydew.common.routes.model.CreateLogResponse
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getServer
import me.znotchill.honeydew.common.routes.model.ValidationErrorDto
import me.znotchill.honeydew.host.backend.ApiManager
import me.znotchill.honeydew.host.backend.config.ConfigManager
import me.znotchill.honeydew.host.backend.routes.validators.createLogValidator
import me.znotchill.honeydew.host.backend.utils.getHumanTimestamp
import java.util.UUID

object CreateLogRoute : RouteModule {
    override fun register(application: Application) {
        application.routing {
            post("/api/servers/{id}/logs") {
                val id = call.parameters["id"]
                val request = call.receive<CreateLogRequest>()

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

                val validation = createLogValidator.validate(request)
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
                    return@post
                }

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

                val logModel = LogModel(
                    channelId = request.channelId,
                    id = UUID.randomUUID().toString(),
                    message = request.message,
                    timestamp = getHumanTimestamp(),
                    metadata = request.metadata
                )

                server.cache.appendLog(logModel)

                call.respond(
                    HttpStatusCode.OK,
                    CreateLogResponse(
                        success = true,
                        messageId = logModel.id
                    )
                )
            }
        }
    }
}

