package com.rafacasari.mod.cobbledex.api

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.giveOrDropItemStack
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.utils.MiscUtils.appendWithSeparator
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import net.minecraft.world.item.ItemStack
import net.minecraft.core.registries.BuiltInRegistries as Registries
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.server.level.ServerPlayer as ServerPlayerEntity

class PokedexRewardHistory(val received: MutableList<String> = mutableListOf()) : PlayerDataExtension {
    override fun name(): String = NAME_KEY

    companion object {
        const val NAME_KEY = "Cobbledex_RewardHistory"
        private val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .create()

        /**
         * Get [PokedexRewardHistory] for [player]
         */
        fun getPlayerRewards(player: ServerPlayerEntity): PokedexRewardHistory {
            val playerData = Cobblemon.playerDataManager.getGenericData(player)
            val history = playerData.extraData.getOrPut(NAME_KEY) {
                PokedexRewardHistory(mutableListOf())
            } as PokedexRewardHistory

            return history
        }


        /**
         * Check available rewards for [player] and automatically give it
         */
        internal fun checkRewards(player: ServerPlayerEntity) {
            if(!Cobbledex.getConfig().CaughtRewards || !Cobbledex.serverInitialized)
                return

            val playerData = Cobblemon.playerDataManager.getGenericData(player)
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
                    val identifier = Identifier.parse(reward.itemId)
                    val item = Registries.ITEM.get(identifier)
                    val itemStack = ItemStack(item, reward.quantity)

                    player.giveOrDropItemStack(itemStack)
                    val itemNameBuilder = mutableListOf(
                        "${reward.quantity}x".text().bold(),
                        itemStack.hoverName.copy().bold()
                    ).appendWithSeparator(" ")

                    val message = cobbledexTextTranslation("reward_received", itemNameBuilder, reward.pokemonCaught.toString().text().bold())
                    player.sendSystemMessage(message)
                    history.received.add(reward.id)
                    // Give item
                    needToSave = true
                }
            }

            if (needToSave)
                Cobblemon.playerDataManager.saveSingle(playerData, PlayerInstancedDataStoreTypes.GENERAL)
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