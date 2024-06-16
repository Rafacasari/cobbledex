package com.rafacasari.mod.cobbledex.utils

import net.minecraft.network.PacketByteBuf

object PacketUtils {
    fun PacketByteBuf.writeNullableIntRange(range: IntRange?) {
        this.writeBoolean(range != null)
        range?.let {
            this.writeInt(it.first)
            this.writeInt(it.last)
        }
    }

    fun PacketByteBuf.readNullableIntRange(): IntRange? {
        return if(this.readBoolean()) {
            val start = this.readInt()
            val endInclusive = this.readInt()
            return IntRange(start, endInclusive)
        } else null
    }


    fun PacketByteBuf.writeIntRange(range: IntRange) {
        range.let {
            this.writeInt(it.first)
            this.writeInt(it.last)
        }
    }

    fun PacketByteBuf.readIntRange(): IntRange {
        val start = this.readInt()
        val endInclusive = this.readInt()
        return IntRange(start, endInclusive)
    }

    fun PacketByteBuf.writeNullableFloat(float: Float?) {
        this.writeBoolean(float != null)
        float?.let {
            this.writeFloat(it)
        }
    }

    fun PacketByteBuf.readNullableFloat() : Float? {
        return if(this.readBoolean())
            this.readFloat()
        else null
    }


    fun PacketByteBuf.writeNullableInt(int: Int?) {
        this.writeBoolean(int != null)
        int?.let {
            this.writeInt(it)
        }
    }

    fun PacketByteBuf.readNullableInt() : Int? {
        return if(this.readBoolean())
            this.readInt()
        else null
    }

    fun PacketByteBuf.writeNullableBool(bool: Boolean?) {
        this.writeBoolean(bool != null)
        bool?.let {
            this.writeBoolean(it)
        }
    }

    fun PacketByteBuf.readNullableBool() : Boolean? {
        return if(this.readBoolean())
            this.readBoolean()
        else null
    }
}