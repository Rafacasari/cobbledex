package com.rafacasari.mod.cobbledex.client.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource

object OpenCollectionCommand : IClientCommandInterface {
    override fun <A: CommandSource, T: CommandDispatcher<A>> register(dispatcher: T) {


        val command = createLiteralArgument<A>("poke_collection")
            .executes {
                _ -> execute()
            }

        dispatcher.register(command)

    }

    private fun execute(): Int {
        MinecraftClient.getInstance().send {
            CobbledexCollectionGUI.show()
        }
        
        return Command.SINGLE_SUCCESS
    }

}