package com.rafacasari.mod.cobbledex.network.template

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.rafacasari.mod.cobbledex.network.IEncodable
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeIntRange
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class SerializableItemDrop() : IEncodable {

    lateinit var item: Identifier
    lateinit var dropMethod: String
    lateinit var quantityRange: IntRange
    var percentage: Float = 0f

    constructor(entry : ItemDropEntry) : this()
    {
        this.item = entry.item
        this.dropMethod = entry.dropMethod?.methodName ?: ""
        this.quantityRange = entry.quantityRange ?: IntRange(entry.quantity, entry.quantity)
        this.percentage = entry.percentage
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(item)
        buffer.writeString(dropMethod)
        buffer.writeIntRange(quantityRange)
        buffer.writeFloat(percentage)
    }

    companion object {
        fun decode(buffer: PacketByteBuf) : SerializableItemDrop {

            val itemDrop = SerializableItemDrop()
            itemDrop.item = buffer.readIdentifier()
            itemDrop.dropMethod = buffer.readString()
            itemDrop.quantityRange = buffer.readIntRange()
            itemDrop.percentage = buffer.readFloat()

            return itemDrop
        }
    }

}