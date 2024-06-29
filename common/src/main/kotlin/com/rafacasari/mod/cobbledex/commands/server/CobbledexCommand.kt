package com.rafacasari.mod.cobbledex.commands.server

import com.cobblemon.mod.common.api.permission.CobblemonPermission
import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.CobbledexConfig
import com.rafacasari.mod.cobbledex.commands.arguments.SettingArgumentSuggestion
import com.rafacasari.mod.cobbledex.network.client.packets.OpenCobbledexPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties

object CobbledexCommand : IServerCommandInterface {

    private val CONFIG_COBBLEDEX_PERMISSION = CobblemonPermission("commands.cobbledex.config", PermissionLevel.ALL_COMMANDS)
    private val OPEN_COBBLEDEX_PERMISSION = CobblemonPermission("commands.cobbledex.show", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
    private val OPEN_COLLECTION_PERMISSION = CobblemonPermission("commands.cobbledex.collection", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        try {
            val pokemonProperty = dispatcher.createArgument("properties", PokemonPropertiesArgumentType.properties())
            val propertyName =
                dispatcher.createArgument("option", StringArgumentType.word()).suggests(SettingArgumentSuggestion())
            val propertyValue =
                dispatcher.createArgument("value", StringArgumentType.word()).suggests(SettingArgumentSuggestion())

            val command = createLiteralArgument("cobbledex")
                .then(CommandManager.literal("collection").permission(OPEN_COLLECTION_PERMISSION).executes { ctx ->
                    openCollection(ctx)
                })
                // Add config options here
                .then(
                    CommandManager.literal("config").permission(CONFIG_COBBLEDEX_PERMISSION)
                        .then(propertyName.executes { ctx -> getSetting(ctx) }
                            .then(propertyValue.executes { ctx ->
                                applySetting(ctx)
                            }))
                )

                //
                .then(
                    CommandManager.literal("show").permission(OPEN_COBBLEDEX_PERMISSION)
                        .then(pokemonProperty.executes { ctx ->
                            showCobbledexCommand(ctx)
                        })
                )

            dispatcher.register(command)
        } catch (e: Exception) {
            logError("Error while registering cobbledex command!")
            e.printStackTrace()
        }
    }
    private val UNKNOWN_PROPERTY_EXCEPTION = SimpleCommandExceptionType("Unknown property!".text().red())
    private val WRONG_VALUE_EXCEPTION = SimpleCommandExceptionType("Unknown property!".text().red())

    private fun getSetting(ctx: CommandContext<ServerCommandSource>): Int {

        val option = StringArgumentType.getString(ctx, "option")

        val property = CobbledexConfig::class.memberProperties.find { it.name.lowercase() == option.lowercase() } ?: throw UNKNOWN_PROPERTY_EXCEPTION.create()
        val getValue = property.get(Cobbledex.getConfig()).toString()
        ctx.source.sendMessage(cobbledexTextTranslation("commands.current_setting", option.text().bold(), getValue.text().bold()))


        return Command.SINGLE_SUCCESS
    }


    private fun applySetting(ctx: CommandContext<ServerCommandSource>): Int {


        val option = StringArgumentType.getString(ctx, "option")
        val value = StringArgumentType.getString(ctx, "value").lowercase()

        val property = CobbledexConfig::class.memberProperties.find { it.name.lowercase() == option.lowercase() } ?: throw UNKNOWN_PROPERTY_EXCEPTION.create()

        val config = Cobbledex.getConfig()
        if (property is KMutableProperty1 && property.returnType == typeOf<Boolean>()) {
            if (value != "true" && value != "false") throw WRONG_VALUE_EXCEPTION.create()

            property.setter.call(config, value.toBoolean())
            val getValue = property.get(config).toString()
            ctx.source.sendMessage(cobbledexTextTranslation("commands.successfully_applied", option.text().bold(), getValue.text().bold()))
            Cobbledex.saveConfig()

            config.syncEveryone()
        }



        return Command.SINGLE_SUCCESS
    }

    private fun openCollection(ctx: CommandContext<ServerCommandSource>): Int {
        ctx.source.player?.let { player ->
            // TODO: Create Show Collection packet
        }

        return Command.SINGLE_SUCCESS
    }

    private val NO_POKEMON_EXCEPTION = SimpleCommandExceptionType("Please provide a Pokémon!".text().red())

    private fun showCobbledexCommand(context: CommandContext<ServerCommandSource>): Int {
        val properties = PokemonPropertiesArgumentType.getPokemonProperties(context, "properties")
        if (properties.species == null) {
            throw NO_POKEMON_EXCEPTION.create()
        }

        context.source.player?.let { player ->
            val species = PokemonSpecies.getByName(properties.species!!)
            if (species != null)
                OpenCobbledexPacket(species.getForm(properties.aspects)).sendToPlayer(player)
        }


        return Command.SINGLE_SUCCESS
    }
}