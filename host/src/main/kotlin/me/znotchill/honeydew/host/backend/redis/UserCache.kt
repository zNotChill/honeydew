package me.znotchill.honeydew.host.backend.redis

import io.lettuce.core.api.sync.RedisCommands
import me.znotchill.honeydew.common.database.model.UserRoleModel
import me.znotchill.honeydew.common.database.model.UserStatusModel
import me.znotchill.honeydew.host.backend.database.DatabaseManager.getUser
import me.znotchill.honeydew.host.backend.database.tables.Users
import me.znotchill.honeydew.host.backend.redis.interfaces.RedisBacked
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object UserCacheStore {
    private val cacheMap = ConcurrentHashMap<String, UserCache>()

    fun get(id: String): UserCache? = cacheMap[id]
    fun getAll() = cacheMap

    fun getOrCreate(id: String, redis: RedisCommands<String, String>): UserCache {
        return cacheMap.computeIfAbsent(id) {
            val user = UserCache(id, redis)
            user.populate()
            user
        }
    }
}

class UserCache(
    val id: String,
    override val redis: RedisCommands<String, String>,
    override val prefix: String = "user:$id"
): RedisBacked {
    var username: String by redisString("username")
    var status: UserStatusModel by redisJson("status", UserStatusModel.serializer())

    var role: UserRoleModel by redisJson("role", UserRoleModel.serializer())

    var discordId: String by redisString("discordId")
    var discordUsername: String by redisString("discordUsername")
    var discordAvatar: String by redisString("discordAvatar")
    var discordAccessToken: String by redisString("discordAccessToken")
    var accessToken: String by redisString("accessToken")

    fun populate() {
        val user = getUser(UUID.fromString(id)) ?: return

        username = user.username
        status = user.status
        role = user.role
        discordId = user.discordId
        discordUsername = user.discordUsername
        discordAvatar = user.discordAvatar
        discordAccessToken = user.discordAccessToken
        accessToken = user.accessToken
    }

    fun flushToDatabase() {
        val uuid = UUID.fromString(id)
        getUser(uuid) ?: return

        transaction {
            Users.update({ Users.id eq uuid }) {
                it[username] = this@UserCache.username
                it[status] = this@UserCache.status
                it[role] = this@UserCache.role
                it[discordId] = this@UserCache.discordId
                it[discordUsername] = this@UserCache.discordUsername
                it[discordAvatar] = this@UserCache.discordAvatar
                it[discordAccessToken] = this@UserCache.discordAccessToken
                it[accessToken] = this@UserCache.accessToken
            }
        }
    }
}