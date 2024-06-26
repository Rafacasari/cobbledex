package com.rafacasari.mod.cobbledex.items

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.isLookingAt
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.client.item.TooltipContext
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.world.World
import com.rafacasari.mod.cobbledex.CobbledexConstants
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import com.rafacasari.mod.cobbledex.utils.cobbledexTextTranslation
import net.minecraft.client.gui.screen.Screen
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.TextContent
import net.minecraft.util.math.Box

class CobbledexItem(settings: Settings) : Item(settings) {
    companion object {
        val totalPokemonDiscovered: Int
            get() {
                return CobbledexCollectionGUI.discoveredList.size
            }
    }

    private val totalPokemonCount: Int
        get() = PokemonSpecies.implemented.size


    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext)
    {

        val percentage = "%.2f%%".format((totalPokemonDiscovered.toDouble() / totalPokemonCount) * 100)
        val translation = cobbledexTextTranslation("tooltip_description.discovered", totalPokemonDiscovered.toString().text().formatted(Formatting.GREEN), totalPokemonCount.toString(), percentage)
        tooltip.add(translation)


        tooltip.add(MutableText.of(TextContent.EMPTY))
        if (Screen.hasShiftDown()) {
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions1"))
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions2"))
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions3"))
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions4"))
        } else {
            tooltip.add(cobbledexTextTranslation("tooltip_description.press_shift").formatted(Formatting.GREEN))
        }


        super.appendTooltip(stack, world, tooltip, context)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)

        if (world.isClient && user.isSneaking) {
            CobbledexCollectionGUI.show()
            return TypedActionResult.pass(itemStack)
        }

        if (!user.isSneaking) {
            // Better entity detection from official Pokédex
            val entity = user.world
                .getOtherEntities(user, Box.of(user.pos, 16.0, 16.0, 16.0))
                .filter { user.isLookingAt(it, stepDistance = 0.1F) }
                .minByOrNull { it.distanceTo(user) }

            if (entity != null) {
                if (entity !is PokemonEntity) {
                    if (world.isClient)
                        user.sendMessage(Text.translatable(CobbledexConstants.invalid_entity))

                    return TypedActionResult.fail(itemStack)
                }

                val target = entity.pokemon

                val discoveryRegister = CobbledexCollectionGUI.discoveredList[target.species.showdownId()]?.contains(target.form.formOnlyShowdownId())
                if (world.isClient && discoveryRegister == true) {
                    CobbledexGUI.openCobbledexScreen(target.form, target.aspects)
                    return TypedActionResult.success(itemStack, false)
                }

                if (user is ServerPlayerEntity) {
                    Cobbledex.registerPlayerDiscovery(user, target.form, target.shiny, DiscoveryRegister.RegisterType.SEEN)
                    return TypedActionResult.success(itemStack)
                }
            } else if(world.isClient) {
                if (CobbledexGUI.previewPokemon != null)
                    CobbledexGUI.openCobbledexScreen()
                else CobbledexCollectionGUI.show()
            }

        }

        return super.use(world, user, hand)
    }
}