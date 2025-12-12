package me.znotchill.honeydew.client

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import me.znotchill.honeydew.client.api.ApiManager
import me.znotchill.honeydew.client.config.ConfigManager
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.main.KSpigot
import net.axay.kspigot.runnables.task
import java.io.File

class HoneydewClient : KSpigot() {
    companion object {
        lateinit var protocolManager: ProtocolManager
        lateinit var instance: HoneydewClient
    }

    override fun startup() {
        instance = this
        protocolManager = ProtocolLibrary.getProtocolManager()

        saveDefaultConfigIfNeeded("application.conf")

        val config = ConfigManager.appConfig
        ApiManager.id = config.server.id
        ApiManager.key = config.server.key
        ApiManager.url = config.host.url
        ApiManager.port = config.host.port
        ApiManager.adminKey = config.host.key

        task(
            sync = false,
            delay = 0,
            period = 20
        ) {
            ApiManager.setPlayersAsync(onlinePlayers.toList())
        }
    }

    override fun shutdown() {
        // Plugin shutdown logic
    }

    private fun saveDefaultConfigIfNeeded(fileName: String) {
        val dataFolderFile = File(dataFolder, fileName)

        if (!dataFolderFile.exists()) {
            dataFolder.mkdirs()

            val inputStream = this::class.java.classLoader.getResourceAsStream(fileName)
            if (inputStream == null) {
                logger.warning("Could not find $fileName in resources.")
                return
            }

            dataFolderFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            logger.info("$fileName has been copied to plugin data folder.")
        }
    }
}
