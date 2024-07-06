package com.rafacasari.mod.cobbledex.commands.server

import com.cobblemon.mod.common.api.permission.CobblemonPermission
import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.pc
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.CobbledexBuildDetails
import com.rafacasari.mod.cobbledex.CobbledexConfig
import com.rafacasari.mod.cobbledex.api.CobbledexCoopDiscovery
import com.rafacasari.mod.cobbledex.api.CobbledexDiscovery
import com.rafacasari.mod.cobbledex.api.PokedexRewardHistory
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.commands.arguments.SettingArgumentSuggestion
import com.rafacasari.mod.cobbledex.network.client.packets.OpenCobbledexPacket
import com.rafacasari.mod.cobbledex.network.client.packets.OpenDiscoveryPacket
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCollectionDataPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.appendWithSeparator
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import com.rafacasari.mod.cobbledex.utils.MiscUtils.openUrl
import com.rafacasari.mod.cobbledex.utils.MiscUtils.toMutableText
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties

object CobbledexCommand : IServerCommandInterface {

    private val AUTO_PERMISSION = CobblemonPermission("commands.cobbledex.auto", PermissionLevel.ALL_COMMANDS)
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

            val playersArgument = CommandManager.argument("players", EntityArgumentType.players())

            val command = createLiteralArgument("cobbledex")
                .executes {
                    ctx -> executeCreditCommand(ctx)
                }
                // Add config options here
                .then(
                    CommandManager.literal("config").permission(CONFIG_COBBLEDEX_PERMISSION)
                        .then(propertyName.executes { ctx -> getSetting(ctx) }
                            .then(propertyValue.executes { ctx ->
                                applySetting(ctx)
                            }))
                )
                // Show Collection
                .then(CommandManager.literal("collection").permission(OPEN_COLLECTION_PERMISSION).executes { ctx ->
                    openCollection(ctx)
                })

                // Show Cobbledex
                .then(
                    CommandManager.literal("show").permission(OPEN_COBBLEDEX_PERMISSION)
                        .then(pokemonProperty.executes { ctx ->
                            showCobbledexCommand(ctx)
                        })
                )

                // Auto get commands
                .then(
                    CommandManager.literal("auto")
                        .permission(AUTO_PERMISSION)
                        .then(playersArgument.executes { ctx ->
                            executeAuto(ctx)
                        })
                )

            dispatcher.register(command)
        } catch (e: Exception) {
            logError("Error while registering cobbledex command!")
            e.printStackTrace()
        }
    }

    private fun executeCreditCommand(ctx: CommandContext<ServerCommandSource>): Int {
        val creditsMessage: MutableList<MutableText> = mutableListOf()
        creditsMessage.add("Cobblemon Pokedex ".text().formatted(Formatting.BOLD, Formatting.RED) + "v${CobbledexBuildDetails.VERSION}".text().formatted(Formatting.WHITE, Formatting.BOLD))

        val creditBuilder: MutableList<MutableText> = mutableListOf()
        creditBuilder.add(Text.literal("Made with"))
        creditBuilder.add(Text.literal("❤").formatted(Formatting.RED))
        creditBuilder.add(Text.literal("by"))
        creditBuilder.add(Text.literal("Rafa").formatted(Formatting.RED, Formatting.BOLD))
        creditBuilder.add(Text.literal("and"))
        creditBuilder.add(Text.literal("Cobblemon Community").formatted(Formatting.RED, Formatting.BOLD))
        creditsMessage.add(creditBuilder.appendWithSeparator(" "))

        val downloadLine: MutableList<MutableText> = mutableListOf()
        downloadLine.add(Text.literal("Download at:"))
        downloadLine.add(Text.literal("GitHub").bold().aqua().openUrl("https://github.com/rafacasari/cobbledex/releases"))
        downloadLine.add(Text.literal("|"))
        downloadLine.add(Text.literal("Modrinth").bold().aqua().openUrl("https://modrinth.com/mod/cobblemon-pokedex"))
        downloadLine.add(Text.literal("|"))
        downloadLine.add(Text.literal("CurseForge").bold().aqua().openUrl("https://curseforge.com/minecraft/mc-mods/cobbledex"))
        creditsMessage.add(downloadLine.appendWithSeparator(" "))

        ctx.source.sendMessage(creditsMessage.toMutableText())
        return Command.SINGLE_SUCCESS
    }

    private fun executeAuto(ctx: CommandContext<ServerCommandSource>): Int {
        val players = EntityArgumentType.getPlayers(ctx, "players")
        players.forEach { player ->
            try {
                val discovery = CobbledexDiscovery.getPlayerData(player)

                discovery.registers.forEach { entry ->
                    entry.value.forEach { formEntry ->
                        CobbledexCoopDiscovery.addOrUpdateWithoutSaving(entry.key, formEntry.key, formEntry.value.isShiny, formEntry.value.status)
                    }
                }

                val pc = player.pc()
                pc.forEach { pokemon ->
                    discovery.addOrUpdate(
                        player,
                        pokemon.form,
                        pokemon.shiny,
                        DiscoveryRegister.RegisterType.CAUGHT,
                        fireEvents = false
                    )
                    CobbledexCoopDiscovery.addOrUpdateCoopWithoutSaving(pokemon.form, pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT)
                }

                val party = player.party()
                party.forEach { pokemon ->
                    discovery.addOrUpdate(
                        player,
                        pokemon.form,
                        pokemon.shiny,
                        DiscoveryRegister.RegisterType.CAUGHT,
                        fireEvents = false
                    )
                    CobbledexCoopDiscovery.addOrUpdateCoopWithoutSaving(pokemon.form, pokemon.shiny, DiscoveryRegister.RegisterType.CAUGHT)
                }

                if (Cobbledex.getConfig().CoopMode)
                    CobbledexCoopDiscovery.getDiscovery()?.registers?.let {
                        ReceiveCollectionDataPacket(it).sendToPlayer(player)
                    }
                else
                    ReceiveCollectionDataPacket(discovery.registers).sendToPlayer(player)
            } catch (_: Exception) {
                // Suppress any error
            }

            PokedexRewardHistory.checkRewards(player)
        }

        CobbledexCoopDiscovery.save()
        ctx.source.sendMessage(Text.literal("Successfully updated player(s)").bold().green())
        return Command.SINGLE_SUCCESS
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

            if (property.name == CobbledexConfig::CoopMode.name) {
                ctx.source.server.playerManager.playerList.forEach { player ->
                    if (config.CoopMode)
                        CobbledexCoopDiscovery.getDiscovery()?.registers?.let {
                            ReceiveCollectionDataPacket(it).sendToPlayer(player)
                        }
                    else {
                        val discovery = CobbledexDiscovery.getPlayerData(player)
                        ReceiveCollectionDataPacket(discovery.registers).sendToPlayer(player)
                    }
                }
            }

            config.syncEveryone()
        }
        return Command.SINGLE_SUCCESS
    }

    private fun openCollection(ctx: CommandContext<ServerCommandSource>): Int {
        ctx.source.player?.let { player ->
            OpenDiscoveryPacket().sendToPlayer(player)
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