package com.rafacasari.mod.cobbledex.api

import com.cobblemon.mod.common.Cobblemon.playerData as CobblemonPlayerData
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.giveOrDropItemStack
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.utils.MiscUtils.appendWithSeparator
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class PokedexRewardHistory(val received: MutableList<String> = mutableListOf()) : PlayerDataExtension {
    override fun name(): String = NAME_KEY

    companion object {
        const val NAME_KEY = "Cobbledex_RewardHistory"
        private val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .create()

        fun getPlayerRewards(player: ServerPlayerEntity): PokedexRewardHistory {
            val playerData= CobblemonPlayerData.get(player)
            val history = playerData.extraData.getOrPut(NAME_KEY) {
                PokedexRewardHistory(mutableListOf())
            } as PokedexRewardHistory

            return history
        }

        fun checkRewards(player: ServerPlayerEntity) {
            if(!Cobbledex.getConfig().CaughtRewards)
                return

            val playerData= CobblemonPlayerData.get(player)
            var needToSave = false

            val history = playerData.extraData.getOrPut(NAME_KEY) {
                // Add an empty reward history
                needToSave = true
                PokedexRewardHistory(mutableListOf())
            } as PokedexRewardHistory

            val totalCaught = CobbledexDiscovery.getPlayerData(player).getTotalCaught()
            val possibleRewards = Cobbledex.getRewardManager().rewards.filter {
                totalCaught >= it.pokemonCaught
            }

            possibleRewards.forEach { reward ->
                if (!history.received.contains(reward.id))
                {
                    val identifier = Identifier(reward.itemId)
                    val item = Registries.ITEM.get(identifier)
                    val itemStack = ItemStack(item, reward.quantity)

                    player.giveOrDropItemStack(itemStack)
                    val itemNameBuilder = mutableListOf(
                        "${reward.quantity}x".text().bold(),
                        MutableText.of(item.name.content).bold()
                    ).appendWithSeparator(" ")

                    val message = cobbledexTextTranslation("reward_received", itemNameBuilder, reward.pokemonCaught.toString().text().bold())
                    player.sendMessage(message)
                    history.received.add(reward.id)
                    // Give item
                    needToSave = true
                }
            }

            if (needToSave)
                CobblemonPlayerData.saveSingle(playerData)
        }
    }

    override fun serialize(): JsonObject {
        val jsonObject = GSON.toJsonTree(this).asJsonObject
        jsonObject.addProperty(PlayerDataExtension.NAME_KEY, this.name())
        return jsonObject
    }

    override fun deserialize(json: JsonObject): PlayerDataExtension {
        return GSON.fromJson(json, PokedexRewardHistory::class.java)
    }
}