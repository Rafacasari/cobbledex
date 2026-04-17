package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.resources.ResourceLocation as Identifier

class ReceiveCollectionDataPacket internal constructor(val discoveredList: MutableMap<String, MutableMap<String, DiscoveryRegister>>):
    INetworkPacket<ReceiveCollectionDataPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeMap(discoveredList,
            { speciesBuffer, speciesName ->
                speciesBuffer.writeUtf(speciesName)
            },
            { bff, value ->
                bff.writeMap(value,
                    { formBuffer, formName ->
                        formBuffer.writeUtf(formName)
                    },
                    { formBuffer, formValue ->
                        formValue.encode(formBuffer)
                    })
            })
    }

    companion object {
        val ID = Identifier.fromNamespaceAndPath("cobbledex", "receive_collection_data")
        fun decode(reader: PacketByteBuf): ReceiveCollectionDataPacket {

            val discoveredList = reader.readMap({ speciesNameReader -> speciesNameReader.readUtf() },
                { formsMap ->
                    formsMap.readMap({ formNameReader -> formNameReader.readUtf() },
                        { form ->
                            DiscoveryRegister.decode(form)
                        })
                })

            return ReceiveCollectionDataPacket(discoveredList)
        }
    }
}
