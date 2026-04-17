package com.rafacasari.mod.cobbledex.network.server.packets

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.resources.ResourceLocation as Identifier

class ClaimRewardPacket internal constructor(val rewardId: String) : INetworkPacket<ClaimRewardPacket> {

    companion object  {
        val ID = cobbledexResource("claim_reward")

        fun decode(reader: PacketByteBuf) : ClaimRewardPacket {
            return ClaimRewardPacket(reader.readUtf())
        }
    }

    override val id: Identifier = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUtf(rewardId)
    }
}