package me.znotchill.honeydew.host.backend.routes

import me.znotchill.honeydew.common.api.ApiMessage
import me.znotchill.honeydew.common.api.ApiResponse
import me.znotchill.honeydew.host.backend.interfaces.RouteModule
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import me.znotchill.honeydew.common.routes.model.StatusResponse

object StatusRoute : RouteModule {
    override fun register(application: Application) {
        application.routing {
            get("/api/status") {
                call.respond(
                    ApiResponse.from(ApiMessage.SERVER_ONLINE, StatusResponse(
                        version = "Beehive 1.0.0 (Honeydew Host)"
                    ))
                )
            }
        }
    }
}

