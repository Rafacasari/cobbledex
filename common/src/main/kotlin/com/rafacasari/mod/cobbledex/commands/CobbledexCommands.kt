package com.rafacasari.mod.cobbledex.commands

import com.mojang.brigadier.CommandDispatcher
import com.rafacasari.mod.cobbledex.client.commands.OpenCobbledexCommand
import net.minecraft.command.CommandSource

object CobbledexCommands {

    // Server side commands
    //fun registerServer(_: CommandDispatcher<ServerCommandSource>, registry: CommandRegistryAccess, selection: CommandManager.RegistrationEnvironment) {
    //}

    // Client side commands
    fun <A: CommandSource, T: CommandDispatcher<A>> registerClient(dispatcher: T) {
        OpenCobbledexCommand.register(dispatcher)
    }
}