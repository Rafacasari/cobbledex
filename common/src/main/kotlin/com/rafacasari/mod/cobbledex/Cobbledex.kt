package com.rafacasari.mod.cobbledex

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtensionRegistry
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.onClick
import com.cobblemon.mod.common.api.text.onHover
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.util.server
import com.rafacasari.mod.cobbledex.api.CobbledexCoopDiscovery
import com.rafacasari.mod.cobbledex.api.CobbledexDiscovery
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.rafacasari.mod.cobbledex.network.client.packets.OpenCobbledexPacket
import com.rafacasari.mod.cobbledex.network.client.packets.AddToCollectionPacket
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCollectionDataPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.WorldSavePath
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Paths

object Cobbledex {
    private lateinit var config: CobbledexConfig
    fun getConfig() : CobbledexConfig = config

    const val MOD_ID : String = "cobbledex"

    private const val VERSION = CobbledexBuildDetails.VERSION
    private const val CONFIG_PATH = "config/$MOD_ID/settings.json"

    val LOGGER: Logger = LoggerFactory.getLogger("Cobbledex")
    lateinit var implementation: CobbledexImplementation

    private var eventsCreated: Boolean = false
    fun preInitialize(implementation: CobbledexImplementation) {
        logInfo("Initializing Cobbledex $VERSION...")
        Cobbledex.implementation = implementation

        implementation.registerItems()
        loadConfig()

        PlatformEvents.SERVER_STARTED.subscribe { serverEvent ->
            logInfo("Server initialized...")

            PlayerDataExtensionRegistry.register(CobbledexDiscovery.NAME_KEY, CobbledexDiscovery::class.java)
            val serverPath = serverEvent.server.getSavePath(WorldSavePath.ROOT).toAbsolutePath()
            CobbledexCoopDiscovery.load(Paths.get(serverPath.toString(), "cobbledex-coop.json").toString())

            if (!eventsCreated) {
                CobblemonEvents.STARTER_CHOSEN.subscribe(Priority.LOW) {
                    registerPlayerDiscovery(it.player, it.pokemon.form, it.pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT)

                    if (getConfig().GiveCobbledexItemOnStarterChosen) {
                        val itemStack = ItemStack(CobbledexConstants.COBBLEDEX_ITEM, 1)
                        it.player.giveItemStack(itemStack)
                    }
                }

                CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST) {
                    registerPlayerDiscovery(it.player, it.pokemon.form, it.pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT)
                }

                CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOW) {
                    val player = it.pokemon.getOwnerPlayer()

                    if (player != null)
                        registerPlayerDiscovery(player, it.pokemon.form, it.pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT)
                }

                CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.LOW) {
                    serverEvent.server.playerManager.getPlayer(it.tradeParticipant1.uuid)?.let { player ->
                        val pokemon = it.tradeParticipant1Pokemon
                        registerPlayerDiscovery(player, pokemon.form, pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT)
                    }

                    serverEvent.server.playerManager.getPlayer(it.tradeParticipant2.uuid)?.let { player ->
                        val pokemon = it.tradeParticipant2Pokemon
                        registerPlayerDiscovery(player, pokemon.form, pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT)
                    }
                }

                // This should prevent events from being added more than once
                eventsCreated = true
            }
        }

        PlatformEvents.CLIENT_PLAYER_LOGOUT.subscribe {
            CobbledexConstants.Client.discoveredList.clear()
        }

        PlatformEvents.CLIENT_PLAYER_LOGIN.subscribe {
            CobbledexGUI.onServerJoin()
            CobbledexCollectionGUI.needReload = true
        }

        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe { login: ServerPlayerEvent.Login ->
            if (config.CoopMode) {
                val discoveryData = CobbledexCoopDiscovery.getDiscovery()
                if (discoveryData != null) {
                    ReceiveCollectionDataPacket(discoveryData.registers).sendToPlayer(login.player)
                }
            }
            else {
                val cobbledexData = CobbledexDiscovery.getPlayerData(login.player)
                val registers = cobbledexData.registers
                ReceiveCollectionDataPacket(registers).sendToPlayer(login.player)
            }

            getConfig().syncPlayer(login.player)
        }
    }

//    fun isClient() : Boolean {
//        return implementation.environment() == Environment.CLIENT
//    }
//
//    fun isServer() : Boolean {
//        return implementation.environment() == Environment.SERVER
//    }

    fun registerPlayerDiscovery(player: ServerPlayerEntity, formData: FormData?, isShiny: Boolean, type: DiscoveryRegister.RegisterType): ActionResult
    {
        if (formData == null)
            return ActionResult.PASS

        val isANewDiscovery = CobbledexDiscovery.addOrUpdatePlayer(player, formData, isShiny, type) { newEntry ->
            AddToCollectionPacket(formData, newEntry).sendToPlayer(player)
        }

        val isANewCoopDiscovery = CobbledexCoopDiscovery.addOrUpdateCoop(formData, isShiny, type) { newEntry ->
            if (config.CoopMode)
                AddToCollectionPacket(formData, newEntry).sendToAllPlayers()
        }

        if(isANewCoopDiscovery && config.CoopMode) {
            val translation = cobbledexTextTranslation("new_pokemon_discovered_coop", Text.literal(player.gameProfile.name).bold(), formData.species.translatedName.bold().formatted(Formatting.GREEN).onClick {
                OpenCobbledexPacket(formData).sendToPlayer(it)
            }.onHover(cobbledexTextTranslation("click_to_open_cobbledex")))

            server()?.let { server ->
                server.playerManager.playerList.forEach { serverPlayer ->
                    serverPlayer.sendMessage(translation)
                }
            }
        }

        if(isANewDiscovery && !config.CoopMode) {
            val translation = cobbledexTextTranslation("new_pokemon_discovered", formData.species.translatedName.bold().formatted(Formatting.GREEN).onClick {
                OpenCobbledexPacket(formData).sendToPlayer(it)
            }.onHover(cobbledexTextTranslation("click_to_open_cobbledex")))

            player.sendMessage(translation)
        }

        return ActionResult.SUCCESS
    }

    private fun loadConfig() {
        val configFile = File(CONFIG_PATH)
        configFile.parentFile.mkdirs()

        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                this.config = CobbledexConfig.GSON.fromJson(fileReader, CobbledexConfig::class.java)
                fileReader.close()
            } catch (error: Exception) {
                LOGGER.error("Failed to load the config! Using default config")
                this.config = CobbledexConfig()
                error.printStackTrace()
            }
        } else {
            this.config = CobbledexConfig()
        }

        config.lastSavedVersion = VERSION
        this.saveConfig()
    }

    fun saveConfig() {
        try {
            val fileWriter = FileWriter(File(CONFIG_PATH))
            CobbledexConfig.GSON.toJson(this.config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: Exception) {
            LOGGER.error("Failed to save the config! Please consult the following stack trace:")
            exception.printStackTrace()
        }
    }
}