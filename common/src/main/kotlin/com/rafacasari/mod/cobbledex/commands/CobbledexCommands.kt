package com.rafacasari.mod.cobbledex.commands

import com.mojang.brigadier.CommandDispatcher
import com.rafacasari.mod.cobbledex.commands.server.CobbledexCommand
import net.minecraft.commands.CommandBuildContext as CommandRegistryAccess
import net.minecraft.commands.SharedSuggestionProvider as CommandSource
import net.minecraft.commands.Commands.CommandSelection
import net.minecraft.commands.CommandSourceStack as ServerCommandSource

object CobbledexCommands {
    // Server side commands
    fun registerServer(dispatcher: CommandDispatcher<ServerCommandSource>, registry: CommandRegistryAccess, selection: CommandSelection) {
        CobbledexCommand.register(dispatcher)
    }

    // Client side commands
    fun <A: CommandSource, T: CommandDispatcher<A>> registerClient(dispatcher: T) {
    }
}