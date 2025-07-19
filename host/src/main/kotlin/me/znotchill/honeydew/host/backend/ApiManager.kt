package me.znotchill.honeydew.host.backend

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.znotchill.honeydew.host.backend.config.ConfigManager
import me.znotchill.honeydew.host.backend.database.DatabaseManager
import me.znotchill.honeydew.host.backend.redis.RedisManager
import me.znotchill.honeydew.host.backend.redis.ServerCacheStore
import me.znotchill.honeydew.host.backend.redis.UserCacheStore
import me.znotchill.honeydew.host.backend.routes.AuthRoutes
import me.znotchill.honeydew.host.backend.routes.CreateLogChannelRoute
import me.znotchill.honeydew.host.backend.routes.CreateLogRoute
import me.znotchill.honeydew.host.backend.routes.CreateServerRoute
import me.znotchill.honeydew.host.backend.routes.GetLogsRoute
import me.znotchill.honeydew.host.backend.routes.GetPlayersRoute
import me.znotchill.honeydew.host.backend.routes.StatusRoute
import me.znotchill.honeydew.host.backend.routes.UpdatePlayersRoute
import me.znotchill.honeydew.host.backend.utils.generateSecureToken

val cfg = ConfigManager.appConfig
object ApiManager {
    val jsonSerializer = Json {
        prettyPrint = true
        isLenient = false
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    val logger = KotlinLogging.logger {}

    fun getBaseUrl(): String {
        return "http://${cfg.server.host}:${cfg.server.port}"
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun Application.apiModule() {
        install(ContentNegotiation) {
            json(jsonSerializer)
        }

        StatusRoute.register(this)
        CreateServerRoute.register(this)
        UpdatePlayersRoute.register(this)
        GetPlayersRoute.register(this)
        CreateLogRoute.register(this)
        CreateLogChannelRoute.register(this)
        GetLogsRoute.register(this)
        AuthRoutes.register(this)

        GlobalScope.launch {
            while (true) {
                delay(10_000L)
                ServerCacheStore.getAll().forEach {
                    it.value.flushToDatabase()
                }
                UserCacheStore.getAll().forEach {
                    it.value.flushToDatabase()
                }
            }
        }
    }

    fun main() {
        if (cfg.api.key.length != 86) {
            logger.error { "No valid api key detected. Generating another." }
            val key = generateSecureToken()
            println(key)
        }

        embeddedServer(Netty, port = cfg.server.port, host = cfg.server.host) {
            apiModule()
        }.start(wait = true)
    }
}

fun main() {
    RedisManager.init()
    DatabaseManager.init()
    ApiManager.main()
}