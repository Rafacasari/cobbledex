package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ReceiveCollectionDataPacket internal constructor(val discoveredList: MutableMap<String, MutableMap<String, DiscoveryRegister>>):
    INetworkPacket<ReceiveCollectionDataPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeMap(discoveredList,
            { speciesBuffer, speciesName ->
                speciesBuffer.writeString(speciesName)
            },
            { bff, value ->
                bff.writeMap(value,
                    { formBuffer, formName ->
                        formBuffer.writeString(formName)
                    },
                    { formBuffer, formValue ->
                        formValue.encode(formBuffer)
                    })
            })
    }

    companion object {
        val ID = Identifier("cobbledex", "receive_collection_data")
        fun decode(reader: PacketByteBuf): ReceiveCollectionDataPacket {

            val discoveredList = reader.readMap({ speciesNameReader -> speciesNameReader.readString() },
                { formsMap ->
                    formsMap.readMap({ formNameReader -> formNameReader.readString() },
                        { form ->
                            DiscoveryRegister.decode(form)
                        })
                })

            return ReceiveCollectionDataPacket(discoveredList)
        }
    }
}