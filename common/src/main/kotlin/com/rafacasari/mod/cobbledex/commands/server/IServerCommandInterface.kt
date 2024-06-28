package com.rafacasari.mod.cobbledex.commands.server

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

interface IServerCommandInterface {

    // Interface methods
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>)

    fun createLiteralArgument(literal: String?): LiteralArgumentBuilder<ServerCommandSource> {
        return LiteralArgumentBuilder.literal(literal)
    }

    fun <T> CommandDispatcher<ServerCommandSource>.createArgument(name: String?, type: ArgumentType<T>?) : RequiredArgumentBuilder<ServerCommandSource, T> {
        return RequiredArgumentBuilder.argument(name, type)
    }
}