package me.znotchill.honeydew.common.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class ValidationErrorDto(
    val field: String,
    val message: String
)