package me.znotchill.honeydew.client.config

import com.typesafe.config.ConfigFactory
import me.znotchill.honeydew.client.HoneydewClient
import me.znotchill.honeydew.client.config.model.AppConfig
import me.znotchill.honeydew.client.config.model.HostConfig
import me.znotchill.honeydew.client.config.model.ServerConfig
import java.io.File

object ConfigManager {
    private val config = ConfigFactory.parseFile(
        File(
            HoneydewClient.instance.dataFolder, "application.conf"
        )
    )

    val appConfig: AppConfig by lazy {
        val serverConfig = ServerConfig(
            id = config.getString("server.id"),
            key = config.getString("server.key")
        )
        val hostConfig = HostConfig(
            url = config.getString("host.url"),
            port = config.getInt("host.port"),
            key = config.getString("host.key"),
        )
        AppConfig(serverConfig, hostConfig)
    }
}