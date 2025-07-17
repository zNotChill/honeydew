package me.znotchill.honeydew.common.database.model

import kotlinx.serialization.Serializable

@Serializable
data class LogChannelModel(
    val id: String,
    val name: String,
    val scope: LogScope,
    val description: String
)

enum class LogScope {
    SERVER,
    GLOBAL
}