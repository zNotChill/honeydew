package me.znotchill.honeydew.host.backend.interfaces

import io.konform.validation.Validation
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import me.znotchill.honeydew.common.api.ApiMessage
import me.znotchill.honeydew.common.api.ApiResponse
import me.znotchill.honeydew.common.routes.model.ValidationErrorDto
import me.znotchill.honeydew.host.backend.config.ConfigManager

open class RouteModule {
    open fun register(application: Application) {}

    fun ApplicationCall.hasValidAdminKey(): Boolean {
        val adminKey = request.headers["X-Admin-Key"]
        val isValid = adminKey == ConfigManager.appConfig.api.key
        return isValid
    }

    suspend fun ApplicationCall.requireAdminKey() {
        if (!hasValidAdminKey()) {
            respond(
                HttpStatusCode.Unauthorized,
                ApiResponse.from(
                    ApiMessage.INVALID_CREDENTIALS, null
                )
            )
        }
    }

    suspend inline fun <reified T : Any> ApplicationCall.tryParsingBody(): T? {
        return try {
            receive<T>()
        } catch (e: Exception) {
            respond(
                HttpStatusCode.BadRequest,
                ApiResponse.from(ApiMessage.INVALID_BODY, null)
            )
            null
        }
    }

    suspend inline fun <reified T : Any> ApplicationCall.parseAndValidate(validator: Validation<T>): T? {
        val body = tryParsingBody<T>()
        if (body == null) return null

        val validation = validator.validate(body)
        if (!validation.errors.isEmpty()) {
            val errors = validation.errors.map {
                ValidationErrorDto(
                    field = it.dataPath,
                    message = it.message
                )
            }

            respond(
                HttpStatusCode.BadRequest,
                ApiResponse.from(
                    ApiMessage.ERROR,
                    details = errors
                )
            )

            return null
        }

        return body
    }
}