package com.rafacasari.mod.cobbledex.api.adapters
//
//import com.google.gson.*
//import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
//import com.rafacasari.mod.cobbledex.utils.logInfo
//import java.lang.reflect.Type
//import java.util.AbstractMap.SimpleEntry
//
//class DiscoveryRegisterAdapter : JsonSerializer<Map.Entry<String, DiscoveryRegister>>, JsonDeserializer<Map.Entry<String, DiscoveryRegister>> {
//    override fun serialize(src: Map.Entry<String, DiscoveryRegister>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
//        logInfo("Serializing")
//
//        val jsonObject = JsonObject()
//        jsonObject.addProperty("speciesWithForm", src.key)
//        jsonObject.addProperty("isShiny", src.value.isShiny)
//        jsonObject.addProperty("status", src.value.status.name)
//        return jsonObject
//    }
//
//    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Map.Entry<String, DiscoveryRegister> {
//        logInfo("De-serializing")
//
//        val jsonObject = json.asJsonObject
//        val speciesWithForm = jsonObject.get("speciesWithForm").asString
//        val isShiny = jsonObject.get("isShiny").asBoolean
//        val status = DiscoveryRegister.RegisterType.valueOf(jsonObject.get("status").asString)
//
//        return SimpleEntry(speciesWithForm, DiscoveryRegister(isShiny, status))
//    }
//}