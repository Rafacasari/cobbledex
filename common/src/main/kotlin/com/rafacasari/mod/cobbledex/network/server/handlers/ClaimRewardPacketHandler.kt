package com.rafacasari.mod.cobbledex.network.server.handlers

import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.giveOrDropItemStack
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.api.CobbledexDiscovery
import com.rafacasari.mod.cobbledex.api.PokedexRewardHistory
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.packets.ClaimRewardPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.appendWithSeparator
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logWarn
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

object ClaimRewardPacketHandler: IServerNetworkPacketHandler<ClaimRewardPacket> {
    override fun handle(packet: ClaimRewardPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        if(!Cobbledex.getConfig().CaughtRewards)
        {
            logWarn("${player.name.string} tried to claim a reward (${packet.rewardId}) but CaughtRewards is disabled on this server")
            return
        }

        val playerHistory = PokedexRewardHistory.getPlayerRewards(player)
        // Check if player already have this reward
        if (playerHistory.received.contains(packet.rewardId)) {
            logWarn("${player.name.string} tried to claim a reward (${packet.rewardId}) but they already got this reward")
            return
        }

        val rewardManager = Cobbledex.getRewardManager()
        val targetReward = rewardManager.rewards.firstOrNull { it.id == packet.rewardId}

        // Check if this reward actually exists
        if (targetReward == null)
        {
            logWarn("${player.name.string} tried to claim a reward (${packet.rewardId}) but this reward doesn't exist!")
            return
        }

        val playerDiscovery = CobbledexDiscovery.getPlayerData(player)
        // Check if player meet the reward requirements
        if (playerDiscovery.getTotalCaught() < targetReward.pokemonCaught) {
            logWarn("${player.name.string} tried to claim a reward (${packet.rewardId}) but doesn't meet the requirements!")
            return
        }

        val identifier = Identifier(targetReward.itemId)
        val item = Registries.ITEM.get(identifier)
        val itemStack = ItemStack(item, targetReward.quantity)

        // Give reward for player
        player.giveOrDropItemStack(itemStack)
        val itemNameBuilder = mutableListOf(
            "${targetReward.quantity}x".text().bold(),
            MutableText.of(item.name.content).bold()
        ).appendWithSeparator(" ")

        val message = cobbledexTextTranslation("reward_received", itemNameBuilder, targetReward.pokemonCaught.toString().text().bold())
        // Send a message telling our player that they got a reward
        player.sendMessage(message)

        // Add to player history
        playerHistory.received.add(targetReward.id)
    }
}