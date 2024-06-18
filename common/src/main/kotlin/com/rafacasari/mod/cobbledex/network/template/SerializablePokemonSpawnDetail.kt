package com.rafacasari.mod.cobbledex.network.template

import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.rafacasari.mod.cobbledex.network.server.IEncodable
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIntRange
import net.minecraft.network.PacketByteBuf

class SerializablePokemonSpawnDetail() : IEncodable {

    var id = ""
    var weight: Float = -1f
    var levelRange: IntRange? = null

    var conditions: List<SerializableSpawnCondition>? = null
    var antiConditions: List<SerializableSpawnCondition>? = null

    constructor(pokemonSpawnDetail: PokemonSpawnDetail) : this()
    {
        this.id = pokemonSpawnDetail.id
        this.weight = pokemonSpawnDetail.weight
        this.levelRange = pokemonSpawnDetail.levelRange
        this.conditions = pokemonSpawnDetail.conditions.map {
            SerializableSpawnCondition(it)
        }
        this.antiConditions = pokemonSpawnDetail.anticonditions.map {
            SerializableSpawnCondition(it)
        }
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(id)
        buffer.writeFloat(weight)
        buffer.writeNullableIntRange(levelRange)

        buffer.writeCollection(conditions ?: listOf()) {
            buff, value -> value.encode(buff)
        }

        buffer.writeCollection(antiConditions ?: listOf()) {
                buff, value -> value.encode(buff)
        }
    }

    companion object {
        fun decode(buffer: PacketByteBuf) : SerializablePokemonSpawnDetail {
            val spawnDetail = SerializablePokemonSpawnDetail()

            spawnDetail.id = buffer.readString()
            spawnDetail.weight = buffer.readFloat()
            spawnDetail.levelRange = buffer.readNullableIntRange()

            spawnDetail.conditions = buffer.readList { buf ->
                SerializableSpawnCondition.decode(buf)
            }

            spawnDetail.antiConditions = buffer.readList { buf ->
                SerializableSpawnCondition.decode(buf)
            }

            return spawnDetail
        }
    }

}

