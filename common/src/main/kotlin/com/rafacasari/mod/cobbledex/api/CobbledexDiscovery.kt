

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
import com.rafacasari.mod.cobbledex.api.events.DiscoveryEvent
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCollectionDataPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import net.minecraft.server.network.ServerPlayerEntity

@Suppress("unused")
/**
 * A player-data that contains all Pokédex data (discovered/caught list)
 *
 * [registers] = MutableMap<**Species.showdownId**, MutableMap<**FormData.formOnlyShowdownId**, **DiscoveryRegister**>>
 *
 */
class CobbledexDiscovery(val registers: MutableMap<String, MutableMap<String, DiscoveryRegister>> = mutableMapOf()): PlayerDataExtension {

    companion object {
        const val NAME_KEY = "${Cobbledex.MOD_ID}_discovery"

        private val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .create()

        /**
         * Get [CobbledexDiscovery] for [player]
         */
        fun getPlayerData(player: ServerPlayerEntity): CobbledexDiscovery {
            val data = playerData.get(player)

            val cobbledexData = data.extraData.getOrPut(NAME_KEY) {
                val discovery = CobbledexDiscovery()

                val pc = player.pc()
                pc.forEach { pokemon ->
                    discovery.addOrUpdate(
                        player,
                        pokemon.form,
                        pokemon.shiny,
                        DiscoveryRegister.RegisterType.CAUGHT,
                        null,
                        fireEvents = false
                    )
                }

                val party = player.party()
                party.forEach { pokemon ->
                    discovery.addOrUpdate(
                        player,
                        pokemon.form,
                        pokemon.shiny,
                        DiscoveryRegister.RegisterType.CAUGHT,
                        null,
                        fireEvents = false
                    )
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

            val isNewRegister = cobbledexData.addOrUpdate(
                player,
                form,
                isShiny,
                status,
                update
            )

            playerData.saveSingle(data)
            return isNewRegister
        }
    }

    /**
     * Return true if player caught the given [FormData]
     */
    fun caught(form: FormData): Boolean = registers[form.species.showdownId()]?.get(form.formOnlyShowdownId())?.status == DiscoveryRegister.RegisterType.CAUGHT

    /**
     * Return true if player discovered/seen the given [FormData]
     */
    fun discovered(form: FormData): Boolean = registers[form.species.showdownId()]?.get(form.formOnlyShowdownId())?.status == DiscoveryRegister.RegisterType.SEEN

    private fun getRegister(showdownId: String): MutableMap<String, DiscoveryRegister>? {
        return registers[showdownId]
    }

    /**
     * Get player total number of Forms discovered
     */
    fun getTotalDiscovered(): Int {
        return registers.values.flatMap { it.values }.count()
    }

    /**
     * Get player total number of Forms caught
     */
    fun getTotalCaught(): Int {
        return registers.values.flatMap { it.values }.count { it.status == DiscoveryRegister.RegisterType.CAUGHT }
    }

    /**
     * Add or update a player discovery
     *
     * [update] is **optional** and is a callback called after adding into list
     *
     * [fireEvents] determine if events in [CobbledexEvents] should be fired
     */
    fun addOrUpdate(player: ServerPlayerEntity, formData: FormData, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: ((DiscoveryRegister) -> Unit)? = null, fireEvents: Boolean = true): Boolean {
        val species = formData.species.showdownId()
        val form = formData.formOnlyShowdownId()

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

                    if (fireEvents)
                        CobbledexEvents.NEW_FORM_CAUGHT.post(DiscoveryEvent.OnFormDiscoveryEvent(player, formRegister, formData))
                }

                update?.invoke(formRegister)
                return false
            } else {
                // New form
                val newRegister =  DiscoveryRegister(isShiny, status, discoverTimestamp, caughtTimestamp)
                currentRegister[form] = newRegister
                update?.invoke(newRegister)

                if (fireEvents) {
                    CobbledexEvents.NEW_FORM_DISCOVERED.post(DiscoveryEvent.OnFormDiscoveryEvent(player, newRegister, formData))
                    if (status == DiscoveryRegister.RegisterType.CAUGHT)
                        CobbledexEvents.NEW_FORM_CAUGHT.post(DiscoveryEvent.OnFormDiscoveryEvent(player, newRegister, formData))
                }
                return true
            }
        } else {
            // New pokemon
            val newRegister = DiscoveryRegister(isShiny, status, discoverTimestamp, caughtTimestamp)
            registers[species] = mutableMapOf(form to newRegister)
            update?.invoke(newRegister)

            CobbledexEvents.NEW_SPECIES_DISCOVERED.post(DiscoveryEvent.OnSpeciesDiscoveryEvent(player, newRegister, formData.species))
            CobbledexEvents.NEW_FORM_DISCOVERED.post(DiscoveryEvent.OnFormDiscoveryEvent(player, newRegister, formData))

            if (fireEvents && status == DiscoveryRegister.RegisterType.CAUGHT) {
                CobbledexEvents.NEW_SPECIES_CAUGHT.post(DiscoveryEvent.OnSpeciesDiscoveryEvent(player, newRegister, formData.species))
                CobbledexEvents.NEW_FORM_CAUGHT.post(DiscoveryEvent.OnFormDiscoveryEvent(player, newRegister, formData))
            }
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