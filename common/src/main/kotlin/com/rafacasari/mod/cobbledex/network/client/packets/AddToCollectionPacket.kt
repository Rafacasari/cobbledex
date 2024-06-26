package com.rafacasari.mod.cobbledex.network.client.packets

import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class AddToCollectionPacket internal constructor(val species: String, val form: String, val entry: DiscoveryRegister):
    INetworkPacket<AddToCollectionPacket> {

    constructor(form: FormData, entry: DiscoveryRegister) : this(form.species.showdownId(), form.formOnlyShowdownId(), entry)

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(species)
        buffer.writeString(form)
        entry.encode(buffer)
    }

    companion object {
        val ID = Identifier("cobbledex", "add_to_collection")
        fun decode(reader: PacketByteBuf): AddToCollectionPacket {
            val species = reader.readString()
            val form = reader.readString()
            val entry = DiscoveryRegister.decode(reader)

            return AddToCollectionPacket(species, form, entry)
        }
    }
}