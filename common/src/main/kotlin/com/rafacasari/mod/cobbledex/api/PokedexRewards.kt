package com.rafacasari.mod.cobbledex.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rafacasari.mod.cobbledex.Cobbledex.MOD_ID
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class PokedexRewards(val rewards: List<PokedexReward>) {
    companion object {
        private val GSON: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        private lateinit var instance: PokedexRewards
        fun getInstance() : PokedexRewards {
            if (!this::instance.isInitialized)
                load()

            return instance
        }

        private fun getDefaultRewards() : List<PokedexReward> {
            return listOf(
                PokedexReward("15", 15, "cobblemon:ancient_poke_ball", 15),
                PokedexReward("30", 30, "cobblemon:thunder_stone", 1),
                PokedexReward("45", 45, "cobblemon:ancient_great_ball", 15),
                PokedexReward("60", 60, "cobblemon:exp_candy_s", 5),
                PokedexReward("75", 75, "cobblemon:fire_stone", 1),
                PokedexReward("90", 90, "cobblemon:net_ball", 15),
                PokedexReward("105", 105, "cobblemon:water_stone", 1),
                PokedexReward("120", 120, "cobblemon:nest_ball", 15),
                PokedexReward("135", 135, "cobblemon:ancient_ultra_ball", 30),
                PokedexReward("150", 150, "cobblemon:exp_candy_s", 5),
                PokedexReward("165", 165, "cobblemon:quick_ball", 15),
                PokedexReward("180", 180, "cobblemon:leaf_stone", 1),
                PokedexReward("195", 195, "cobblemon:dusk_ball", 15),
                PokedexReward("210", 210, "cobblemon:exp_candy_s", 5),
                PokedexReward("225", 225, "cobblemon:thunder_stone", 1),
                PokedexReward("240", 240, "cobblemon:dive_ball", 15),
                PokedexReward("255", 255, "cobblemon:exp_candy_m", 3),
                PokedexReward("270", 270, "cobblemon:luxury_ball", 15),
                PokedexReward("285", 285, "cobblemon:sun_stone", 1),
                PokedexReward("300", 300, "cobblemon:exp_candy_m", 3),
                PokedexReward("315", 315, "cobblemon:ability_capsule", 1),
                PokedexReward("330", 330, "cobblemon:timer_ball", 15),
                PokedexReward("345", 345, "cobblemon:dusk_stone", 1),
                PokedexReward("360", 360, "cobblemon:exp_candy_m", 3),
                PokedexReward("375", 375, "cobblemon:everstone", 1),
                PokedexReward("390", 390, "cobblemon:fast_ball", 15),
                PokedexReward("405", 405, "cobblemon:ice_stone", 1),
                PokedexReward("420", 420, "cobblemon:destiny_knot", 1),
                PokedexReward("435", 435, "cobblemon:friend_ball", 15),
                PokedexReward("450", 450, "cobblemon:exp_candy_l", 1),
                PokedexReward("465", 465, "cobblemon:shiny_stone", 1),
                PokedexReward("480", 480, "cobblemon:level_ball", 15),
                PokedexReward("495", 495, "cobblemon:exp_candy_l", 1),
                PokedexReward("510", 510, "cobblemon:lure_ball", 15),
                PokedexReward("525", 525, "cobblemon:moon_stone", 1),
                PokedexReward("540", 540, "cobblemon:heavy_ball", 15),
                PokedexReward("555", 555, "cobblemon:exp_candy_xl", 1),
                PokedexReward("570", 570, "cobblemon:moon_ball", 15),
                PokedexReward("585", 585, "cobblemon:dawn_stone", 1),
                PokedexReward("600", 600, "cobblemon:exp_candy_xl", 1),
                PokedexReward("615", 615, "cobblemon:love_ball", 15),
                PokedexReward("633", 633, "cobblemon:beast_ball", 1)
            )
        }

        private const val PATH = "config/$MOD_ID/rewards.json"

        private fun load() {
            val configFile = File(PATH)
            configFile.parentFile.mkdirs()

            if (configFile.exists()) {
                try {
                    val fileReader = FileReader(configFile)
                    instance = GSON.fromJson(fileReader, PokedexRewards::class.java)
                    fileReader.close()
                } catch (error: Exception) {
                    logError("Failed to load Cobbledex Rewards! Using default default rewards")
                    instance = PokedexRewards(getDefaultRewards())
                    error.printStackTrace()
                }
            } else {
                instance = PokedexRewards(getDefaultRewards())
            }

            save()
        }

        private fun save() {
            try {
                val fileWriter = FileWriter(File(PATH))
                GSON.toJson(instance, fileWriter)
                fileWriter.flush()
                fileWriter.close()
            } catch (exception: Exception) {
                logError("Failed to save rewards! Error stack trace:")
                exception.printStackTrace()
            }
        }
    }


    data class PokedexReward(val id: String, val pokemonCaught: Int, val itemId: String, val quantity: Int)
}