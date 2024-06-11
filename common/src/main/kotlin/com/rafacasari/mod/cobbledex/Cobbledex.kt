package com.rafacasari.mod.cobbledex

import com.cobblemon.mod.common.Cobblemon.playerData as CobblemonPlayerData
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtensionRegistry
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.rafacasari.mod.cobbledex.cobblemon.extensions.PlayerDiscovery

object Cobbledex {
    const val MOD_ID : String = "cobbledex"

    val LOGGER: Logger = LoggerFactory.getLogger("Cobbledex")
    lateinit var implementation: CobbledexImplementation


    private var eventsCreated: Boolean = false
    fun preInitialize(implementation: CobbledexImplementation) {
        LOGGER.info("Initializing Cobbledex...")
        Cobbledex.implementation = implementation

        implementation.registerItems()

        // TODO: Make our own event so we don't need to depend on Cobblemon PlatformEvents
        PlatformEvents.SERVER_STARTED.subscribe { _ ->
            LOGGER.info("Cobbledex: Server initialized...")
            PlayerDataExtensionRegistry.register(PlayerDiscovery.NAME_KEY, PlayerDiscovery::class.java)

            if (!eventsCreated) {
                CobblemonEvents.STARTER_CHOSEN.subscribe(Priority.LOW) {
                    registerPlayerDiscovery(it.player, it.pokemon.species)

                    val itemStack = ItemStack(CobbledexConstants.Cobbledex_Item, 1)
                    it.player.giveItemStack(itemStack)
                }

                CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOW) {
                    registerPlayerDiscovery(it.player, it.pokemon.species)
                }

                CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOW) {
                    val player = it.pokemon.getOwnerPlayer()
                    if (player != null) {
                        registerPlayerDiscovery(player, it.pokemon.species)
                    }
                }

                // This should prevent events from being added more than once
                eventsCreated = true
            }
        }
    }

    fun isClient() : Boolean {
        return implementation.environment() == Environment.CLIENT
    }

    fun isServer() : Boolean {
        return implementation.environment() == Environment.SERVER
    }

    fun registerPlayerDiscovery(player: PlayerEntity?, species: Species?): ActionResult
    {
        if (player == null || species == null)
            return ActionResult.PASS

        val playerData = CobblemonPlayerData.get(player)
        val cobbledexData = playerData.extraData.getOrPut(PlayerDiscovery.NAME_KEY) {
            // TODO: Maybe add the player PC/party pokemon in the first discover?
            PlayerDiscovery()
        } as PlayerDiscovery


        if (!cobbledexData.caughtSpecies.contains(species.nationalPokedexNumber)) {
            cobbledexData.caughtSpecies.add(species.nationalPokedexNumber)

            player.sendMessage(Text.literal("You've discovered a new Pokémon: §a" + species.name + "§r"))
        }

        CobblemonPlayerData.saveSingle(playerData)

        return ActionResult.SUCCESS
    }
}