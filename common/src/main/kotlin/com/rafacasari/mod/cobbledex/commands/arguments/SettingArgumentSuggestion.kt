package com.rafacasari.mod.cobbledex.commands.arguments

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.rafacasari.mod.cobbledex.CobbledexConfig
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf


class SettingArgumentSuggestion : SuggestionProvider<ServerCommandSource> {

    private val settingOptions = CobbledexConfig::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }.map { it.name }

    fun getValuesFor(name: String): List<String> {
        val test = CobbledexConfig::class.memberProperties.find { it.visibility == KVisibility.PUBLIC && it.name == name } ?: return listOf()
        val type = test.returnType

        if (type == typeOf<Boolean>())
            return listOf("true", "false")

        return listOf()
    }

    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {

        val values = builder.input.split(" ")

        // If is below 4, means that user is choosing propertyName
        if (values.size < 4)
            return CommandSource.suggestMatching(settingOptions, builder)

        // values[0] = /cobbledex
        // values[1] = config
        // values[2] = propertyName
        // values[4] = propertyValue

        // Return possible values for propertyName (values[2])
        return CommandSource.suggestMatching(getValuesFor(values[2]), builder)
    }

}