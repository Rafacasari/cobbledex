package com.rafacasari.mod.cobbledex.network.client.packets

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import com.rafacasari.mod.cobbledex.network.template.SerializableItemDrop
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonEvolution
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ReceiveCobbledexPacket internal constructor(
    val species: Species?,
    val evolutionList: List<SerializablePokemonEvolution>,
    val preevolutionList: List<Pair<Identifier, Set<String>>>,
    val formList: List<Pair<Identifier, Set<String>>>,
    val spawnDetails: List<SerializablePokemonSpawnDetail>,
    val pokemonDrops: List<SerializableItemDrop>
):
    INetworkPacket<ReceiveCobbledexPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(species != null)
        if (species != null)
            buffer.writeIdentifier(species.resourceIdentifier)

//        buffer.writeCollection(evolutionList) { buff, value ->
//            buff.writeIdentifier(value.first)
//            buff.writeCollection(value.second) { aspectBuffer, aspect ->
//                aspectBuffer.writeString(aspect)
//            }
//        }

        buffer.writeCollection(evolutionList) { buff, value ->
            value.encode(buff)
        }

        buffer.writeCollection(preevolutionList) { buff, value ->
            buff.writeIdentifier(value.first)
            buff.writeCollection(value.second) { aspectBuffer, aspect ->
                aspectBuffer.writeString(aspect)
            }
        }

        buffer.writeCollection(formList) { buff, value ->
            buff.writeIdentifier(value.first)
            buff.writeCollection(value.second) { aspectBuffer, aspect ->
                aspectBuffer.writeString(aspect)
            }
        }

        buffer.writeCollection(spawnDetails) {
            buff, value -> value.encode(buff)
        }

        buffer.writeCollection(pokemonDrops) {
            buff, value -> value.encode(buff)
        }
    }

    companion object{
        val ID = Identifier("cobbledex", "receive_cobbledex")
        fun decode(buffer: PacketByteBuf) : ReceiveCobbledexPacket {
            var bufferSpecies: Species? = null
            if (buffer.readBoolean())
                bufferSpecies = PokemonSpecies.getByIdentifier(buffer.readIdentifier())

//            val evolutionList = buffer.readList { value ->
//                Pair(value.readIdentifier(), value.readList { aspect ->
//                    aspect.readString()
//                }.toSet())
//            }

            val evolutionList = buffer.readList { listReader ->
                SerializablePokemonEvolution.decode(listReader)
            }

            val preEvolutionList = buffer.readList { value ->
                Pair(value.readIdentifier(), value.readList { aspect ->
                    aspect.readString()
                }.toSet())
            }

            val formsList = buffer.readList { value ->
                Pair(value.readIdentifier(), value.readList { aspect ->
                    aspect.readString()
                }.toSet())
            }

            val spawnDetails = buffer.readList {
                value -> SerializablePokemonSpawnDetail.decode(value)
            }

            val pokemonDrops = buffer.readList {
                    value -> SerializableItemDrop.decode(value)
            }

            return ReceiveCobbledexPacket(bufferSpecies, evolutionList, preEvolutionList, formsList, spawnDetails, pokemonDrops)
        }
    }
}