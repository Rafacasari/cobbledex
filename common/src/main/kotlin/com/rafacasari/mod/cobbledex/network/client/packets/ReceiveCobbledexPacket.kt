package com.rafacasari.mod.cobbledex.network.client.packets

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.network.template.SerializableItemDrop
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonEvolution
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.resources.ResourceLocation as Identifier

class ReceiveCobbledexPacket internal constructor(
    val species: Species?,
    val evolutionList: List<SerializablePokemonEvolution>,
    val preevolutionList: List<Pair<Identifier, Set<String>>>,
    val spawnDetails: List<SerializablePokemonSpawnDetail>,
    val pokemonDrops: List<SerializableItemDrop>
):
    INetworkPacket<ReceiveCobbledexPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(species != null)
        if (species != null)
            buffer.writeResourceLocation(species.resourceIdentifier)

        buffer.writeCollection(evolutionList) { buff, value ->
            value.encode(buff)
        }

        buffer.writeCollection(preevolutionList) { buff, value ->
            buff.writeResourceLocation(value.first)
            buff.writeCollection(value.second) { aspectBuffer, aspect ->
                aspectBuffer.writeUtf(aspect)
            }
        }

//        buffer.writeCollection(formList) { buff, value ->
//            buff.writeResourceLocation(value.first)
//            buff.writeCollection(value.second) { aspectBuffer, aspect ->
//                aspectBuffer.writeUtf(aspect)
//            }
//        }

        buffer.writeCollection(spawnDetails) {
            buff, value -> value.encode(buff)
        }

        buffer.writeCollection(pokemonDrops) {
            buff, value -> value.encode(buff)
        }
    }

    companion object{
        val ID = Identifier.fromNamespaceAndPath("cobbledex", "receive_cobbledex")
        fun decode(buffer: PacketByteBuf) : ReceiveCobbledexPacket {
            var bufferSpecies: Species? = null
            if (buffer.readBoolean())
                bufferSpecies = PokemonSpecies.getByIdentifier(buffer.readResourceLocation())

//            val evolutionList = buffer.readList { value ->
//                Pair(value.readResourceLocation(), value.readList { aspect ->
//                    aspect.readUtf()
//                }.toSet())
//            }

            val evolutionList = buffer.readList { listReader ->
                SerializablePokemonEvolution.decode(listReader)
            }

            val preEvolutionList = buffer.readList { value ->
                Pair(value.readResourceLocation(), value.readList { aspect ->
                    aspect.readUtf()
                }.toSet())
            }

//            val formsList = buffer.readList { value ->
//                Pair(value.readResourceLocation(), value.readList { aspect ->
//                    aspect.readUtf()
//                }.toSet())
//            }

            val spawnDetails = buffer.readList {
                value -> SerializablePokemonSpawnDetail.decode(value)
            }

            val pokemonDrops = buffer.readList {
                    value -> SerializableItemDrop.decode(value)
            }

            return ReceiveCobbledexPacket(bufferSpecies, evolutionList, preEvolutionList, spawnDetails, pokemonDrops)
        }
    }
}
