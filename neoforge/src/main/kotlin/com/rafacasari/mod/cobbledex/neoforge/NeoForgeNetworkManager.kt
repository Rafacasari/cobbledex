package com.rafacasari.mod.cobbledex.neoforge

import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.INetworkManager
import com.rafacasari.mod.cobbledex.network.CobbledexNetwork
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import io.netty.buffer.Unpooled
import kotlin.reflect.KClass
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf as RegistryByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.server.level.ServerPlayer as ServerPlayerEntity
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.network.registration.HandlerThread

object NeoForgeNetworkManager : INetworkManager {

    private const val PROTOCOL_VERSION = "1"

    private data class ClientboundRegistration(
        val decode: (PacketByteBuf) -> INetworkPacket<*>,
        val handle: (INetworkPacket<*>) -> Unit
    )

    private data class ServerboundRegistration(
        val decode: (PacketByteBuf) -> INetworkPacket<*>,
        val handle: (INetworkPacket<*>, ServerPlayerEntity) -> Unit
    )

    private val clientboundRegistrations = linkedMapOf<Identifier, ClientboundRegistration>()
    private val serverboundRegistrations = linkedMapOf<Identifier, ServerboundRegistration>()

    override fun registerClientBound() = Unit

    override fun registerServerBound() = Unit

    fun registerMessages(event: RegisterPayloadHandlersEvent) {
        clientboundRegistrations.clear()
        serverboundRegistrations.clear()

        CobbledexNetwork.registerClientBound()
        CobbledexNetwork.registerServerBound()

        val registrar = event.registrar(Cobbledex.MOD_ID)
            .versioned(PROTOCOL_VERSION)
            .executesOn(HandlerThread.NETWORK)

        registrar.playToClient(ClientboundPayload.TYPE, ClientboundPayload.CODEC, IPayloadHandler { payload, _ ->
            val registration = clientboundRegistrations[payload.packetId] ?: return@IPayloadHandler
            val packet = registration.decode(PacketByteBuf(Unpooled.wrappedBuffer(payload.data)))
            registration.handle(packet)
        })

        registrar.playToServer(ServerboundPayload.TYPE, ServerboundPayload.CODEC, IPayloadHandler { payload, context ->
            val registration = serverboundRegistrations[payload.packetId] ?: return@IPayloadHandler
            val player = context.player() as ServerPlayerEntity
            val packet = registration.decode(PacketByteBuf(Unpooled.wrappedBuffer(payload.data)))
            registration.handle(packet, player)
        })
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : INetworkPacket<T>> createClientBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: IClientNetworkPacketHandler<T>
    ) {
        clientboundRegistrations[identifier] = ClientboundRegistration(
            decode = { decoder(it) },
            handle = { packet -> handler.handleOnNettyThread(packet as T) }
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : INetworkPacket<T>> createServerBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: IServerNetworkPacketHandler<T>
    ) {
        serverboundRegistrations[identifier] = ServerboundRegistration(
            decode = { decoder(it) },
            handle = { packet, player -> handler.handleOnNettyThread(packet as T, player.server, player) }
        )
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: INetworkPacket<*>) {
        PacketDistributor.sendToPlayer(player, ClientboundPayload(packet.id, packet.toByteArray()))
    }

    override fun sendPacketToServer(packet: INetworkPacket<*>) {
        PacketDistributor.sendToServer(ServerboundPayload(packet.id, packet.toByteArray()))
    }

    override fun <T : INetworkPacket<*>> asVanillaClientBound(packet: T): Packet<*> {
        return ClientboundCustomPayloadPacket(ClientboundPayload(packet.id, packet.toByteArray()))
    }

    private fun INetworkPacket<*>.toByteArray(): ByteArray {
        val buffer = toBuffer()
        return ByteArray(buffer.readableBytes()).also { data ->
            buffer.getBytes(buffer.readerIndex(), data)
        }
    }

    private data class ClientboundPayload(val packetId: Identifier, val data: ByteArray) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<ClientboundPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<ClientboundPayload>(Identifier.fromNamespaceAndPath(Cobbledex.MOD_ID, "client_payload"))
            val CODEC: StreamCodec<RegistryByteBuf, ClientboundPayload> = StreamCodec.of(
                { buf, payload ->
                    buf.writeResourceLocation(payload.packetId)
                    buf.writeByteArray(payload.data)
                },
                { buf -> ClientboundPayload(buf.readResourceLocation(), buf.readByteArray()) }
            )
        }
    }

    private data class ServerboundPayload(val packetId: Identifier, val data: ByteArray) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<ServerboundPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<ServerboundPayload>(Identifier.fromNamespaceAndPath(Cobbledex.MOD_ID, "server_payload"))
            val CODEC: StreamCodec<RegistryByteBuf, ServerboundPayload> = StreamCodec.of(
                { buf, payload ->
                    buf.writeResourceLocation(payload.packetId)
                    buf.writeByteArray(payload.data)
                },
                { buf -> ServerboundPayload(buf.readResourceLocation(), buf.readByteArray()) }
            )
        }
    }
}
