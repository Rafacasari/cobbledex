package com.rafacasari.mod.cobbledex.network.client.packets

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ReceiveCobbledexPacket internal constructor(
    val species: Species?,
    val evolutionList: List<Identifier>,
    val spawnDetails: List<SerializablePokemonSpawnDetail>
):
    INetworkPacket<ReceiveCobbledexPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(species != null)
        if (species != null)
            buffer.writeIdentifier(species.resourceIdentifier)

        buffer.writeCollection(evolutionList) {
            buff, value -> buff.writeIdentifier(value)
        }

        buffer.writeCollection(spawnDetails) {
            buff, value -> value.encode(buff)
        }
    }

    companion object{
        val ID = Identifier("cobbledex", "receive_cobbledex")
        fun decode(buffer: PacketByteBuf) : ReceiveCobbledexPacket {
            var bufferSpecies: Species? = null
            if (buffer.readBoolean())
                bufferSpecies = PokemonSpecies.getByIdentifier(buffer.readIdentifier())

            val evolutionList = buffer.readList { value -> value.readIdentifier() }

            val spawnDetails = buffer.readList {
                value -> SerializablePokemonSpawnDetail.decode(value)
            }

            return ReceiveCobbledexPacket(bufferSpecies, evolutionList, spawnDetails)
        }
    }
}