package com.rafacasari.mod.cobbledex

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class CobbledexConfig {


    companion object {
        val GSON: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()
    }

    var lastSavedVersion: String = "0.0.1"

    var howToFindEnabled = true
    var showEvolutions = true
    var itemDropsEnabled = true


}
