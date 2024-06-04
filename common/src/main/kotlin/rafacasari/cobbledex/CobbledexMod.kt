package rafacasari.cobbledex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.pokemon.Species
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.architectury.event.events.client.ClientCommandRegistrationEvent
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.server.command.CommandManager
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rafacasari.cobbledex.client.gui.CobbledexGUI
import rafacasari.cobbledex.cobblemon.extensions.CobbledexDataExtension
import java.util.function.Supplier


object CobbledexMod {
    const val MOD_ID : String = "cobbledex"

    val LOGGER: Logger = LoggerFactory.getLogger("Cobbledex")

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

    fun init() {
        LOGGER.info("Initializing Cobbledex...")

        CobbledexRegistries.registerItems()

        LifecycleEvent.SERVER_STARTED.register { server ->
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


        }





//        ClientCommandRegistrationEvent.EVENT.register { dispatcher, _ ->
//            dispatcher.register(LiteralArgumentBuilder.literal<ClientCommandRegistrationEvent.ClientCommandSourceStack>("opencobbledex")
//                .then(RequiredArgumentBuilder.argument("id", IntegerArgumentType.integer()))
//                .executes { ctx ->
//                    val arg = IntegerArgumentType.getInteger(ctx, "id")
//                    CobbledexGUI.openCobbledexScreen(null)
//                    1
//                }
//            )
//        }

    }

    fun registerPlayerDiscovery(player: PlayerEntity?, species: Species?): ActionResult
    {
//        val isClient = MinecraftClient.getInstance().world?.isClient
//        if (isClient != null && isClient)
//        {
//            LOGGER.error("Tried to register a player discovery on Client")
//            return ActionResult.FAIL
//        }

        if (player == null || species == null)
        {
            return ActionResult.PASS
        }

        val playerData = Cobblemon.playerData.get(player)
        var cobbledexData = playerData.extraData[CobbledexDataExtension.NAME_KEY] as? CobbledexDataExtension

        // Create data if it doesn't exist yet
        if (cobbledexData == null)
        {
            // TODO: Register all Pokemon from Cobblemon PC into Cobbledex
            cobbledexData = CobbledexDataExtension()
        }

        if (!cobbledexData.caughtSpecies.contains(species.nationalPokedexNumber)) {
            cobbledexData.caughtSpecies.add(species.nationalPokedexNumber)

            val clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "")
            player.sendMessage(Text.literal("You've discovered a new Pokémon: §a" + species.name + "§r"))
        }

        playerData.extraData[CobbledexDataExtension.NAME_KEY] = cobbledexData
        Cobblemon.playerData.saveSingle(playerData)

        return ActionResult.SUCCESS
    }

}