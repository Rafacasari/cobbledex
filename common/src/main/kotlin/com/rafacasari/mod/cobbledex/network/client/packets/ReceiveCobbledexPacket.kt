package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ReceiveCobbledexPacket internal constructor(
    val evolutionList: List<Identifier>,
    val spawnDetails: List<SerializablePokemonSpawnDetail>
):
    INetworkPacket<ReceiveCobbledexPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
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
            val evolutionList = buffer.readList { value -> value.readIdentifier() }

            val spawnDetails = buffer.readList {
                value -> SerializablePokemonSpawnDetail.decode(value)
            }

            return ReceiveCobbledexPacket(evolutionList, spawnDetails)
        }
    }
}