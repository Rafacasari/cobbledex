package com.rafacasari.mod.cobbledex.cobblemon.extensions
//
//import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
//import com.google.gson.*
//import com.rafacasari.mod.cobbledex.Cobbledex
//
//class PlayerDiscovery(val caughtSpecies: MutableSet<Int> = hashSetOf()): PlayerDataExtension {
//
//    companion object {
//        const val NAME_KEY = Cobbledex.MOD_ID
//
//        private val GSON = GsonBuilder()
//            .disableHtmlEscaping()
//            .create()
//    }
//
//    override fun name(): String {
//        return NAME_KEY
//    }
//
//    override fun serialize(): JsonObject {
//        val jsonObject = GSON.toJsonTree(this).asJsonObject
//        jsonObject.addProperty(PlayerDataExtension.NAME_KEY, this.name())
//        return jsonObject
//    }
//
//    override fun deserialize(json: JsonObject): PlayerDataExtension {
//        return GSON.fromJson(json, PlayerDiscovery::class.java)
//    }
//}