package com.rafacasari.mod.cobbledex.items

import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.isLookingAt
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.CobbledexConstants
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.totalPokemonCaught
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.totalPokemonCount
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.totalPokemonDiscovered
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import net.minecraft.ChatFormatting as Formatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component as Text
import net.minecraft.server.level.ServerPlayer as ServerPlayerEntity
import net.minecraft.world.InteractionHand as Hand
import net.minecraft.world.InteractionResultHolder as TypedActionResult
import net.minecraft.world.entity.player.Player as PlayerEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag as TooltipType
import net.minecraft.world.level.Level as World
import net.minecraft.world.phys.AABB as Box

class CobbledexItem(settings: Item.Properties) : Item(settings) {

    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltip: MutableList<Text>, type: TooltipType) {
        val percentageDiscovered = "%.2f%%".format((totalPokemonDiscovered.toDouble() / totalPokemonCount) * 100)
        val percentageCaught = "%.2f%%".format((totalPokemonCaught.toDouble() / totalPokemonCount) * 100)

        val translationDiscovered = cobbledexTextTranslation(
            "tooltip_description.discovered",
            totalPokemonDiscovered.toString().text().withStyle(Formatting.GREEN),
            totalPokemonCount.toString(),
            percentageDiscovered
        )
        val translationCaught = cobbledexTextTranslation(
            "tooltip_description.caught",
            totalPokemonCaught.toString().text().withStyle(Formatting.GREEN),
            totalPokemonCount.toString(),
            percentageCaught
        )

        tooltip.add(translationDiscovered)
        tooltip.add(translationCaught)
        tooltip.add(Text.empty())

        if (Screen.hasShiftDown()) {
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions1"))
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions2"))
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions3"))
            tooltip.add(cobbledexTextTranslation("tooltip_description.instructions4"))
        } else {
            tooltip.add(cobbledexTextTranslation("tooltip_description.press_shift").withStyle(Formatting.GREEN))
        }

        super.appendHoverText(stack, context, tooltip, type)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getItemInHand(hand)
        val isSneaking = user.isShiftKeyDown

        if (world.isClientSide && isSneaking) {
            CobbledexCollectionGUI.show()
            return TypedActionResult.pass(itemStack)
        }

        if (!isSneaking) {
            val searchBox = Box.ofSize(user.position(), 16.0, 16.0, 16.0)
            val entity = world.getEntities(user, searchBox) { target ->
                user.isLookingAt(target, stepDistance = 0.1F)
            }.minByOrNull { target ->
                target.distanceTo(user)
            }

            if (entity != null) {
                if (entity !is PokemonEntity) {
                    if (world.isClientSide) {
                        user.displayClientMessage(cobbledexTextTranslation("not_a_pokemon"), false)
                    }

                    return TypedActionResult.fail(itemStack)
                }

                val target = entity.pokemon
                if (world.isClientSide) {
                    val discoveryRegister =
                        CobbledexConstants.Client.discoveredList[target.species.showdownId()]?.containsKey(target.form.formOnlyShowdownId()) == true

                    if (discoveryRegister) {
                        CobbledexGUI.openCobbledexScreen(target.form, target.aspects)
                        return TypedActionResult.sidedSuccess(itemStack, true)
                    }
                }

                if (user is ServerPlayerEntity) {
                    Cobbledex.registerPlayerDiscovery(user, target.form, target.shiny, DiscoveryRegister.RegisterType.SEEN)
                    return TypedActionResult.sidedSuccess(itemStack, false)
                }
            } else if (world.isClientSide) {
                if (CobbledexGUI.previewForm != null) {
                    CobbledexGUI.openCobbledexScreen()
                } else {
                    CobbledexCollectionGUI.show()
                }

                return TypedActionResult.sidedSuccess(itemStack, true)
            }
        }

        return super.use(world, user, hand)
    }
}
