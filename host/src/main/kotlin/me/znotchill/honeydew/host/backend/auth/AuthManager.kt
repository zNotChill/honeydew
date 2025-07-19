package me.znotchill.honeydew.host.backend.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.znotchill.honeydew.common.database.model.DiscordUserModel
import me.znotchill.honeydew.host.backend.ApiManager
import me.znotchill.honeydew.host.backend.auth.model.DiscordTokenResponse
import me.znotchill.honeydew.host.backend.config.ConfigManager

object AuthManager {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val discordConfig = ConfigManager.appConfig.discord

    private const val discordAuthorizeUrl = "https://discord.com/api/oauth2/authorize"
    private const val discordTokenUrl = "https://discord.com/api/oauth2/token"
    private const val discordUserUrl = "https://discord.com/api/users/@me"

    fun getCallbackUrl(): String = "${ApiManager.getBaseUrl()}/api/auth/callback"

    fun getLoginUrl(): String {
        val scopes = listOf("identify")
        return URLBuilder(discordAuthorizeUrl).apply {
            parameters.append("client_id", discordConfig.clientId)
            parameters.append("redirect_uri", getCallbackUrl())
            parameters.append("response_type", "code")
            parameters.append("scope", scopes.joinToString(" "))
        }.buildString()
    }

    suspend fun exchangeCodeForToken(code: String): String? {
        val response = client.submitForm(
            url = discordTokenUrl,
            formParameters = Parameters.build {
                append("client_id", discordConfig.clientId)
                append("client_secret", discordConfig.clientSecret)
                append("grant_type", "authorization_code")
                append("code", code)
                append("redirect_uri", getCallbackUrl())
            }
        )

        if (response.status != HttpStatusCode.OK) return null

        val json = response.body<DiscordTokenResponse>()
        return json.access_token.removeSurrounding("\"")
    }

    suspend fun fetchDiscordUser(accessToken: String): DiscordUserModel? {
        val response = client.get(discordUserUrl) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            null
        }
    }
}