package com.rafacasari.mod.cobbledex.fabric

import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.INetworkManager
import com.rafacasari.mod.cobbledex.network.CobbledexNetwork
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import io.netty.buffer.Unpooled
import kotlin.reflect.KClass
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf as RegistryByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer as ServerPlayerEntity

object FabricNetworkManager : INetworkManager {

    private data class ClientboundRegistration(
        val decode: (PacketByteBuf) -> INetworkPacket<*>,
        val handle: (INetworkPacket<*>) -> Unit
    )

    private data class ServerboundRegistration(
        val decode: (PacketByteBuf) -> INetworkPacket<*>,
        val handle: (INetworkPacket<*>, MinecraftServer, ServerPlayerEntity) -> Unit
    )

    private val clientboundRegistrations = linkedMapOf<Identifier, ClientboundRegistration>()
    private val serverboundRegistrations = linkedMapOf<Identifier, ServerboundRegistration>()

    private var payloadTypesRegistered = false
    private var clientReceiverRegistered = false
    private var serverReceiverRegistered = false

    override fun registerClientBound() {
        registerPayloadTypes()
        clientboundRegistrations.clear()

        if (!clientReceiverRegistered) {
            ClientPlayNetworking.registerGlobalReceiver(ClientboundPayload.TYPE) { payload, _ ->
                val registration = clientboundRegistrations[payload.packetId] ?: return@registerGlobalReceiver
                val packet = registration.decode(PacketByteBuf(Unpooled.wrappedBuffer(payload.data)))
                registration.handle(packet)
            }
            clientReceiverRegistered = true
        }

        CobbledexNetwork.registerClientBound()
    }

    override fun registerServerBound() {
        registerPayloadTypes()
        serverboundRegistrations.clear()

        if (!serverReceiverRegistered) {
            ServerPlayNetworking.registerGlobalReceiver(ServerboundPayload.TYPE) { payload, context ->
                val registration = serverboundRegistrations[payload.packetId] ?: return@registerGlobalReceiver
                val packet = registration.decode(PacketByteBuf(Unpooled.wrappedBuffer(payload.data)))
                registration.handle(packet, context.player().server, context.player())
            }
            serverReceiverRegistered = true
        }

        CobbledexNetwork.registerServerBound()
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
            handle = { packet, server, player -> handler.handleOnNettyThread(packet as T, server, player) }
        )
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: INetworkPacket<*>) {
        ServerPlayNetworking.send(player, ClientboundPayload(packet.id, packet.toByteArray()))
    }

    override fun sendPacketToServer(packet: INetworkPacket<*>) {
        ClientPlayNetworking.send(ServerboundPayload(packet.id, packet.toByteArray()))
    }

    override fun <T : INetworkPacket<*>> asVanillaClientBound(packet: T): Packet<*> {
        return ServerPlayNetworking.createS2CPacket(ClientboundPayload(packet.id, packet.toByteArray()))
    }

    private fun registerPayloadTypes() {
        if (payloadTypesRegistered) {
            return
        }

        PayloadTypeRegistry.playS2C().register(ClientboundPayload.TYPE, ClientboundPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(ServerboundPayload.TYPE, ServerboundPayload.CODEC)
        payloadTypesRegistered = true
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
