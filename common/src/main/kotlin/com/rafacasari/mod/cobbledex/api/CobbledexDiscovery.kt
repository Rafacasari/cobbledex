package com.rafacasari.mod.cobbledex.api

import com.cobblemon.mod.common.Cobblemon.playerData
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.pc
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCollectionDataPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import net.minecraft.server.network.ServerPlayerEntity

class CobbledexDiscovery(val registers: MutableMap<String, MutableMap<String, DiscoveryRegister>> = mutableMapOf()): PlayerDataExtension {

    companion object {
        const val NAME_KEY = "${Cobbledex.MOD_ID}_discovery"

        private val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .create()

        fun getPlayerData(player: ServerPlayerEntity): CobbledexDiscovery {
            val data = playerData.get(player)

            val cobbledexData = data.extraData.getOrPut(NAME_KEY) {
                val discovery = CobbledexDiscovery()

                val pc = player.pc()
                pc.forEach { pokemon ->
                    discovery.addOrUpdate(pokemon.species.showdownId(), pokemon.form.formOnlyShowdownId(), pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT, null)
                }

                val party = player.party()
                party.forEach { pokemon ->
                    discovery.addOrUpdate(pokemon.species.showdownId(), pokemon.form.formOnlyShowdownId(), pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT, null)
                }

                ReceiveCollectionDataPacket(discovery.registers).sendToPlayer(player)
                logInfo("Added ${discovery.registers.size} entries in ${player.entityName}'s Cobbledex")
                return@getOrPut discovery
            } as CobbledexDiscovery

            return cobbledexData
        }

        fun addOrUpdatePlayer(player: ServerPlayerEntity, form: FormData, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: (DiscoveryRegister) -> (Unit)): Boolean {
            val data = playerData.get(player)

            val cobbledexData = getPlayerData(player)

            val isNewRegister = cobbledexData.addOrUpdate(form.species.showdownId(), form.formOnlyShowdownId(), isShiny, status, update)

            playerData.saveSingle(data)
            return isNewRegister
        }
    }

    private fun getRegister(showdownId: String): MutableMap<String, DiscoveryRegister>? {
        return registers[showdownId]
    }

    fun addOrUpdate(species: String, form: String, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: ((DiscoveryRegister) -> Unit)? = null): Boolean {
        val currentRegister = getRegister(species)

        val discoverTimestamp = System.currentTimeMillis()
        val caughtTimestamp = if(status == DiscoveryRegister.RegisterType.CAUGHT) discoverTimestamp else null

        if (currentRegister != null) {
            val formRegister = currentRegister[form]
            if (formRegister != null) {
                // Update only if needed
                if (!formRegister.isShiny && isShiny)
                    formRegister.isShiny = true

                // Update only if needed
                if (formRegister.status == DiscoveryRegister.RegisterType.SEEN && status == DiscoveryRegister.RegisterType.CAUGHT) {
                    formRegister.status = DiscoveryRegister.RegisterType.CAUGHT
                    formRegister.caughtTimestamp = caughtTimestamp
                }

                update?.invoke(formRegister)
                return false
            } else {
                // New form
                val newRegister =  DiscoveryRegister(isShiny, status, discoverTimestamp, caughtTimestamp)
                currentRegister[form] = newRegister
                update?.invoke(newRegister)
                return true
            }
        } else {
            // New pokemon
            val newRegister = DiscoveryRegister(isShiny, status, discoverTimestamp, caughtTimestamp)
            registers[species] = mutableMapOf(form to newRegister)
            update?.invoke(newRegister)
            return true
        }
    }

    override fun name(): String {
        return NAME_KEY
    }

    override fun serialize(): JsonObject {
        val jsonObject = GSON.toJsonTree(this).asJsonObject
        jsonObject.addProperty(PlayerDataExtension.NAME_KEY, this.name())
        return jsonObject
    }

    override fun deserialize(json: JsonObject): PlayerDataExtension {
        return GSON.fromJson(json, CobbledexDiscovery::class.java)
    }
}