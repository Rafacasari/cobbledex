package rafacasari.cobbledex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtensionRegistry
import com.cobblemon.mod.common.pokemon.Species
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rafacasari.cobbledex.cobblemon.extensions.PlayerDiscovery
import java.util.function.Supplier

object Cobbledex {
    const val MOD_ID : String = "cobbledex"

    val LOGGER: Logger = LoggerFactory.getLogger("Cobbledex")
    private lateinit var implementation: CobbledexImplementation

    object CobbledexRegistries {
        private val manager: Supplier<RegistrarManager> = Supplier<RegistrarManager> {
            RegistrarManager.get(
                MOD_ID
            )
        }

        private val items: Registrar<Item> = manager.get().get(Registries.ITEM)

        fun registerItems() {
            LOGGER.info("Cobbledex: Registering items...")
            items.register(Identifier(MOD_ID, "cobbledex_item")) {
                CobbledexConstants.Cobbledex_Item
            }
        }
    }

    private var eventsCreated: Boolean = false
    fun init(implementation: CobbledexImplementation) {
        LOGGER.info("Initializing Cobbledex...")
        this.implementation = implementation

        CobbledexRegistries.registerItems()

        LifecycleEvent.SERVER_STARTED.register { _ ->
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

        val playerData = Cobblemon.playerData.get(player)
        val cobbledexData = playerData.extraData.getOrPut(PlayerDiscovery.NAME_KEY) {
            // TODO: Maybe add the player PC/party pokemon in the first discover?
            PlayerDiscovery()
        } as PlayerDiscovery


        if (!cobbledexData.caughtSpecies.contains(species.nationalPokedexNumber)) {
            cobbledexData.caughtSpecies.add(species.nationalPokedexNumber)

            player.sendMessage(Text.literal("You've discovered a new Pokémon: §a" + species.name + "§r"))
        }

        Cobblemon.playerData.saveSingle(playerData)

        return ActionResult.SUCCESS
    }

}