package com.rafacasari.mod.cobbledex.items

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity

import net.minecraft.item.*

import net.minecraft.client.item.TooltipContext
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.world.World
import com.rafacasari.mod.cobbledex.CobbledexConstants
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI

class CobbledexItem(settings: Settings) : Item(settings) {



    override fun useOnEntity(
        itemStack: ItemStack?, player: PlayerEntity?, target: LivingEntity?, hand: Hand?): ActionResult {

        if (player == null || target == null || player.world == null) {
            return ActionResult.FAIL
        }

        if (target !is PokemonEntity) {
            if (player.world.isClient) {
                player.sendMessage(Text.translatable(CobbledexConstants.NotAPokemon))
            }
            return ActionResult.FAIL
        }

        if (player.world.isClient) {
            CobbledexGUI.openCobbledexScreen(target.pokemon)
            return ActionResult.PASS
        }

        return Cobbledex.registerPlayerDiscovery(player, target.pokemon.species)
    }


    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?)
    {
        tooltip?.add(Text.literal("Discovered: §a0§r/100"))

        super.appendTooltip(stack, world, tooltip, context)
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {

        if (user != null && world != null && world.isClient && user.isSneaking) {
            CobbledexGUI.openCobbledexScreen(null)
        }

        return super.use(world, user, hand)
    }



//    override fun use(level: World?, player: PlayerEntity, hand: Hand): InteractionResultHolder<ItemStack> {
//        CobbledexReforged.LOGGER.info("ITEM USED")
//
//        if (level.isClientSide) {
//            CobbledexGUI.openCobbledexScreen()
//        }
//
//        return super.use(level, player, hand)
//    }
}