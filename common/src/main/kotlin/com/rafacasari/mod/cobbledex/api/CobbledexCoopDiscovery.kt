package com.rafacasari.mod.cobbledex.api

import com.cobblemon.mod.common.pokemon.FormData
import com.google.gson.GsonBuilder
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logDebug
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logWarn
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class CobbledexCoopDiscovery(val registers: MutableMap<String, MutableMap<String, DiscoveryRegister>> = mutableMapOf()) {
    companion object {
        private val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        private var isInitialized: Boolean =false
        private lateinit var discovery: CobbledexCoopDiscovery

        private lateinit var filePath: String
        fun getDiscovery() : CobbledexCoopDiscovery?
        {
            if (!isInitialized)
                logWarn("Called getDiscovery before initialization")

            return if (this::discovery.isInitialized) discovery else null
        }

        fun load(path: String) {
            if (isInitialized) {
                logWarn("CobbledexCoopDiscovery.load was called twice!")
                return
            }

            isInitialized = true
            filePath = path

            logInfo("Loading CobbledexCoopDiscovery at \"${filePath}\"...")
            val configFile = File(path)
            configFile.parentFile.mkdirs()

            if (configFile.exists()) {
                try {
                    val fileReader = FileReader(configFile)
                    discovery = GSON.fromJson(fileReader, CobbledexCoopDiscovery::class.java)
                    fileReader.close()
                } catch (error: Exception) {
                    logError("COOP Cobbledex Discovery failed to load, using default file instead!")
                    discovery = CobbledexCoopDiscovery()
                    error.printStackTrace()
                }
            } else {
                discovery = CobbledexCoopDiscovery()
            }

            this.save()
        }

        fun save() {
            if (!isInitialized)
            {
                logWarn("Tried to save CobbledexCoopDiscovery without loading it first")
                return
            }

            if (!this::filePath.isInitialized)
            {
                logWarn("Tried to save CobbledexCoopDiscovery without filePath being initialized")
                return
            }

            val discoveryRegister = getDiscovery()
            if (discoveryRegister == null) {
                logWarn("Tried to save CobbledexCoopDiscovery but discover variable was null (or not initialized)")
                return
            }

            logDebug("Saving ${discoveryRegister.registers.size} entries to $filePath")
            try {
                val fileWriter = FileWriter(File(filePath))
                GSON.toJson(discoveryRegister, fileWriter)
                fileWriter.flush()
                fileWriter.close()
            } catch (exception: Exception) {
                logError("Failed to save the CobbledexCoopDiscovery! Stack trace:")
                exception.printStackTrace()
            }
        }

        fun addOrUpdateWithoutSaving(speciesShowdownId: String, formOnlyShowdownId: String, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: ((DiscoveryRegister) -> Unit)? = null): Boolean {
            val data = getDiscovery()

            var returnValue = false
            if (data != null) returnValue = data.addOrUpdate(speciesShowdownId, formOnlyShowdownId, isShiny, status, update)

            return returnValue
        }

        fun addOrUpdateCoop(speciesShowdownId: String, formOnlyShowdownId: String, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: ((DiscoveryRegister) -> Unit)? = null): Boolean {
            val returnValue = addOrUpdateWithoutSaving(speciesShowdownId, formOnlyShowdownId, isShiny, status, update)

            // Save :D
            save()
            return returnValue
        }

        fun addOrUpdateCoopWithoutSaving(form: FormData, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: ((DiscoveryRegister) -> Unit)? = null): Boolean {
            return addOrUpdateWithoutSaving(form.species.showdownId(), form.formOnlyShowdownId(), isShiny, status, update)
        }

        fun addOrUpdateCoop(form: FormData, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: ((DiscoveryRegister) -> Unit)? = null): Boolean {
            return addOrUpdateCoop(form.species.showdownId(), form.formOnlyShowdownId(), isShiny, status, update)
        }
    }

    private fun getRegister(showdownId: String): MutableMap<String, DiscoveryRegister>? {
        return registers[showdownId]
    }

    // addOrUpdate(showdownId, onlyFormShowdownId)
        // Returns if it is a new entry (meaning that should display a message in chat)
    //
    fun addOrUpdate(species: String, form: String, isShiny: Boolean, status: DiscoveryRegister.RegisterType, update: ((DiscoveryRegister) -> Unit)?): Boolean {
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
}