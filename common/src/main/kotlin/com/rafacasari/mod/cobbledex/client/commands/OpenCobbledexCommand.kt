package com.rafacasari.mod.cobbledex.client.commands

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource

object OpenCobbledexCommand : IClientCommandInterface {
    override fun <A: CommandSource, T: CommandDispatcher<A>> register(dispatcher: T) {

        val pokemonProperty = dispatcher.createArgument("properties", PokemonPropertiesArgumentType.properties())


        val command = createLiteralArgument<A>("cobbledex")
            .executes {
                _ -> execute()
            }
            .then(pokemonProperty.executes {
                ctx -> execute(ctx)
            })

        dispatcher.register(command)

    }

    private val NO_POKEMON_EXCEPTION = SimpleCommandExceptionType("Please provide a Pokémon!".text().red())

    private fun <T> execute(context: CommandContext<T>): Int {

        val properties = PokemonPropertiesArgumentType.getPokemonProperties(context, "properties")
        if (properties.species == null) {
            throw NO_POKEMON_EXCEPTION.create()
        }

        MinecraftClient.getInstance().send {
            val species = PokemonSpecies.getByName(properties.species!!)
            if (species != null) {
                CobbledexGUI.openCobbledexScreen(species.getForm(properties.aspects), properties.aspects)
            }
        }

        return Command.SINGLE_SUCCESS
    }

    private fun execute(): Int {
        MinecraftClient.getInstance().send {
            CobbledexGUI.openCobbledexScreen()
        }
        
        return Command.SINGLE_SUCCESS
    }

}