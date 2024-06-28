package com.rafacasari.mod.cobbledex.commands

import com.mojang.brigadier.CommandDispatcher
import com.rafacasari.mod.cobbledex.commands.server.CobbledexCommand
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CobbledexCommands {

    // Server side commands
    fun registerServer(dispatcher: CommandDispatcher<ServerCommandSource>, registry: CommandRegistryAccess, selection: CommandManager.RegistrationEnvironment) {
        CobbledexCommand.register(dispatcher)
    }

    // Client side commands
    fun <A: CommandSource, T: CommandDispatcher<A>> registerClient(dispatcher: T) {
    }

}