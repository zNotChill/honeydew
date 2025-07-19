package me.znotchill.honeydew.host.backend.database.tables

import me.znotchill.honeydew.common.database.model.UserRoleModel
import me.znotchill.honeydew.common.database.model.UserStatusModel
import me.znotchill.honeydew.host.backend.redis.RedisManager
import me.znotchill.honeydew.host.backend.redis.UserCache
import me.znotchill.honeydew.host.backend.redis.UserCacheStore
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*

object Users : UUIDTable("users") {
    val username = varchar("name", 30)
    val status = enumerationByName("status", length = 10, UserStatusModel::class)

    val role = enumerationByName("role", length = 10, UserRoleModel::class)

    val discordId = varchar("discord_id", 255)
    val discordUsername = varchar("discord_username", 255)
    val discordAvatar = varchar("discord_avatar", 255)

    val discordAccessToken = varchar("discord_access_token", 255)
    val accessToken = varchar("access_token", 255)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var username by Users.username
    var status by Users.status
    var role by Users.role
    var discordId by Users.discordId
    var discordUsername by Users.discordUsername
    var discordAvatar by Users.discordAvatar
    var discordAccessToken by Users.discordAccessToken
    var accessToken by Users.accessToken

    val cache: UserCache
        get() = UserCacheStore.getOrCreate(this.id.value.toString(), RedisManager.commands)
}