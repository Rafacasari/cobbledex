package com.rafacasari.mod.cobbledex.network.server.packets

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ClaimRewardPacket internal constructor(val rewardId: String) : INetworkPacket<ClaimRewardPacket> {

    companion object  {
        val ID = cobbledexResource("claim_reward")

        fun decode(reader: PacketByteBuf) : ClaimRewardPacket {
            return ClaimRewardPacket(reader.readString())
        }
    }

    override val id: Identifier = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(rewardId)
    }
}