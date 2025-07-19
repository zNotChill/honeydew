package me.znotchill.honeydew.host.backend.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.znotchill.honeydew.common.api.ApiMessage
import me.znotchill.honeydew.common.api.ApiResponse
import me.znotchill.honeydew.common.routes.model.CreateUserRequest
import me.znotchill.honeydew.common.routes.model.CreateUserResponse
import me.znotchill.honeydew.host.backend.auth.AuthManager
import me.znotchill.honeydew.host.backend.database.DatabaseManager
import me.znotchill.honeydew.host.backend.interfaces.RouteModule

object AuthRoutes : RouteModule {
    override fun register(application: Application) {
        application.routing {
            get("/login") {
                call.respondRedirect(
                    AuthManager.getLoginUrl()
                )
            }

            get("/api/auth/callback") {
                val code = call.request.queryParameters["code"]
                if (code == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.from(ApiMessage.MISSING_CODE, null)
                    )
                    return@get
                }

                val token = AuthManager.exchangeCodeForToken(code)
                if (token == null) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse.from(ApiMessage.FAILED_TO_GET_TOKEN, null)
                    )
                    return@get
                }

                val discordUser = AuthManager.fetchDiscordUser(token)
                if (discordUser == null) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse.from(ApiMessage.FAILED_TO_GET_USER, null)
                    )
                    return@get
                }

                val request = CreateUserRequest(
                    id = discordUser.id,
                    username = discordUser.username,
                    avatar = discordUser.avatar,
                    accessToken = token
                )
                val user = DatabaseManager.getUserByDiscordId(discordUser.id)

                if (user != null) {
                    user.cache.discordUsername = discordUser.username
                    user.cache.discordAvatar = discordUser.avatar ?: ""
                    user.cache.discordAccessToken = token

                    val response = CreateUserResponse(
                        success = true,
                        accessToken = user.cache.accessToken,
                        userId = user.cache.id,
                    )

                    call.respond(
                        HttpStatusCode.Created,
                        response
                    )
                    return@get
                }

                val response = DatabaseManager.createUser(request)

                call.respond(
                    HttpStatusCode.Created,
                    response
                )
            }
        }
    }
}

