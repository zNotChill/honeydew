package me.znotchill.honeydew.common.api

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val status: String,
    val type: String,
    val message: String,

    // TODO: figure out why this warning isn't suppressable
    @EncodeDefault
    val success: Boolean = true,
    val details: T? = null
) {
    companion object {
        fun <T> from(message: ApiMessage, details: T? = null): ApiResponse<T> = ApiResponse(
            status = message.status,
            type = message.type,
            message = message.message,
            success = message.success,
            details = details
        )
    }
}