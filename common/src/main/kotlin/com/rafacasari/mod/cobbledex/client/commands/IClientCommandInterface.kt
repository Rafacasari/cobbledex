package com.rafacasari.mod.cobbledex.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.command.CommandSource

interface IClientCommandInterface {

    fun <S: CommandSource> createLiteralArgument(literal: String?): LiteralArgumentBuilder<S> {
        return LiteralArgumentBuilder.literal(literal)
    }

    fun <A: CommandSource, T> createArgument(dispatcher: CommandDispatcher<A>, name: String?, type: ArgumentType<T>?): RequiredArgumentBuilder<A, T> {
        return RequiredArgumentBuilder.argument(name, type)
    }

    fun <A: CommandSource, T: CommandDispatcher<A>> register(dispatcher: T)

}