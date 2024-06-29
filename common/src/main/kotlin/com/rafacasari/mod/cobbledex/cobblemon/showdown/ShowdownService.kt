package com.rafacasari.mod.cobbledex.cobblemon.showdown

import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import org.graalvm.polyglot.Context
import java.io.File

object ShowdownService {

    private lateinit var context: Context

    private var initialized: Boolean = false

    private fun createContext() {

        if (initialized) return

        val file = File("showdown/data/typechart.js")
        if (!file.exists()) return

        try {

            context = Context.newBuilder("js")
                .option("engine.WarnInterpreterOnly", "false")
                .build()

            val script = """
                const exports = {};
                const module = { exports: exports };
                ${file.readText()}
                exports.TypeChart;
            """.trimIndent()

            context.eval("js", script)

            initialized = true

        } catch (e: Exception) {
            logError(e.toString())
        }
    }

    fun getTypeChart() : HashMap<String, HashMap<String, Int>> {

        if (!initialized)
            createContext()

        val typeChartConst = context.getBindings("js").getMember("TypeChart")

        val hashMap = HashMap<String, HashMap<String, Int>>()

        if (typeChartConst != null && typeChartConst.hasMembers()) {
            for (elementalKey in typeChartConst.memberKeys) {

                val damageTakenArray = typeChartConst.getMember(elementalKey).getMember("damageTaken")

                // We need to make sure that the elementalTypes are in lowercase, cause Cobblemon want it on lower-case.
                hashMap[elementalKey] = damageTakenArray.memberKeys.associate {
                    it.lowercase() to damageTakenArray.getMember(it).asInt()
                } as HashMap

            }
        }

        return hashMap
    }
}