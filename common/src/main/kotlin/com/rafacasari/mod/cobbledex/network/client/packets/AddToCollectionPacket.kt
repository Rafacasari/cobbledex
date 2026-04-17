package com.rafacasari.mod.cobbledex.network.client.packets

import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.resources.ResourceLocation as Identifier

class AddToCollectionPacket internal constructor(val species: String, val form: String, val entry: DiscoveryRegister):
    INetworkPacket<AddToCollectionPacket> {

    constructor(form: FormData, entry: DiscoveryRegister) : this(form.species.showdownId(), form.formOnlyShowdownId(), entry)

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUtf(species)
        buffer.writeUtf(form)
        entry.encode(buffer)
    }

    companion object {
        val ID = Identifier.fromNamespaceAndPath("cobbledex", "add_to_collection")
        fun decode(reader: PacketByteBuf): AddToCollectionPacket {
            val species = reader.readUtf()
            val form = reader.readUtf()
            val entry = DiscoveryRegister.decode(reader)

            return AddToCollectionPacket(species, form, entry)
        }
    }
}
