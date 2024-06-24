package com.rafacasari.mod.cobbledex

import com.cobblemon.mod.common.Cobblemon.playerData as CobblemonPlayerData
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtensionRegistry
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.onClick
import com.cobblemon.mod.common.api.text.onHover
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.rafacasari.mod.cobbledex.cobblemon.extensions.PlayerDiscovery
import com.rafacasari.mod.cobbledex.network.client.packets.OpenCobbledexPacket
import com.rafacasari.mod.cobbledex.network.client.packets.AddToCollectionPacket
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCollectionDataPacket
import com.rafacasari.mod.cobbledex.utils.cobbledexTextTranslation
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Formatting
import java.io.File
import java.io.FileReader
import java.io.FileWriter

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
        LOGGER.info("Initializing Cobbledex $VERSION...")
        Cobbledex.implementation = implementation

        implementation.registerItems()
        loadConfig()

        // TODO: Make our own event so we don't need to depend on Cobblemon PlatformEvents
        PlatformEvents.SERVER_STARTED.subscribe { _ ->
            LOGGER.info("Cobbledex: Server initialized...")
            PlayerDataExtensionRegistry.register(PlayerDiscovery.NAME_KEY, PlayerDiscovery::class.java)

            if (!eventsCreated) {
                CobblemonEvents.STARTER_CHOSEN.subscribe(Priority.LOW) {
                    registerPlayerDiscovery(it.player, it.pokemon.form)

                    val itemStack = ItemStack(CobbledexConstants.Cobbledex_Item, 1)
                    it.player.giveItemStack(itemStack)
                }

                CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOW) {
                    registerPlayerDiscovery(it.player, it.pokemon.form)
                }

                CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOW) {
                    val player = it.pokemon.getOwnerPlayer()
                    if (player != null) {
                        registerPlayerDiscovery(player, it.pokemon.form)
                    }
                }


                // This should prevent events from being added more than once
                eventsCreated = true
            }
        }

        PlatformEvents.CLIENT_PLAYER_LOGOUT.subscribe {
            CobbledexCollectionGUI.discoveredList = null
        }

        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe { login: ServerPlayerEvent.Login ->

            val playerData = CobblemonPlayerData.get(login.player)
            val cobbledexData = playerData.extraData[PlayerDiscovery.NAME_KEY] as PlayerDiscovery?
            var totalPokemonDiscovered = 0
            if (cobbledexData != null)
                totalPokemonDiscovered = cobbledexData.caughtSpecies.size

            AddToCollectionPacket(totalPokemonDiscovered,).sendToPlayer(login.player)
            ReceiveCollectionDataPacket(cobbledexData?.caughtSpecies?.toList() ?: listOf()).sendToPlayer(login.player)
        }
    }

    fun isClient() : Boolean {
        return implementation.environment() == Environment.CLIENT
    }

    fun isServer() : Boolean {
        return implementation.environment() == Environment.SERVER
    }

    fun registerPlayerDiscovery(player: PlayerEntity?, formData: FormData?): ActionResult
    {
        if (player == null || formData == null)
            return ActionResult.PASS

        val playerData = CobblemonPlayerData.get(player)
        val cobbledexData = playerData.extraData.getOrPut(PlayerDiscovery.NAME_KEY) {
            // TODO: Maybe add the player PC/party pokemon in the first discover?
            PlayerDiscovery()
        } as PlayerDiscovery


        if (!cobbledexData.caughtSpecies.contains(formData.species.nationalPokedexNumber)) {
            cobbledexData.caughtSpecies.add(formData.species.nationalPokedexNumber)

            val translation = cobbledexTextTranslation("new_pokemon_discovered", formData.species.translatedName.bold().formatted(Formatting.GREEN).onClick {
                OpenCobbledexPacket(formData).sendToPlayer(it)
            }.onHover(cobbledexTextTranslation("click_to_open_cobbledex")))

            player.sendMessage(translation)

            if (player is ServerPlayerEntity)
                AddToCollectionPacket(formData.species.nationalPokedexNumber).sendToPlayer(player)
        }

        CobblemonPlayerData.saveSingle(playerData)

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

    private fun saveConfig() {
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