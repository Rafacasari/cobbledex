package com.rafacasari.mod.cobbledex.network.template

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableBool
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableFloat
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableInt
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableBool
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableFloat
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableInt
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIntRange
import com.rafacasari.mod.cobbledex.utils.logError
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

class SerializablePokemonSpawnDetail() : Encodable {

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

class SerializableSpawnCondition() : Encodable {

    constructor(condition: SpawningCondition<*>) : this() {
        this.dimensions = condition.dimensions
        this.biomes = condition.biomes
        this.moonPhase = condition.moonPhase?.ranges
        this.canSeeSky = condition.canSeeSky
        this.minX = condition.minX
        this.minY = condition.minY
        this.minZ = condition.minZ

        this.maxX = condition.maxX
        this.maxY = condition.maxY
        this.maxZ = condition.maxZ

        this.minLight = condition.minLight
        this.maxLight = condition.maxLight

        this.minSkyLight = condition.minSkyLight
        this.maxSkyLight = condition.maxSkyLight

        this.isRaining = condition.isRaining
        this.isThundering = condition.isThundering
        this.timeRange = condition.timeRange

        val structureList = condition.structures?.mapNotNull { structure ->
            var identifier: Identifier? = null
            try {
                structure.ifLeft { id -> identifier = id }
                structure.ifRight { tag -> identifier = tag.id }
            }
            catch (e: Exception) {
                logError("Error while trying to parse structure list")
                logError(e.toString())
            }

            identifier
        }

        this.structures = structureList
    }

    var dimensions: List<Identifier>? = null
    var biomes: MutableSet<RegistryLikeCondition<Biome>>? = null
    var moonPhase: List<IntRange>? = null
    var canSeeSky: Boolean? = null
    var minX: Float? = null
    var minY: Float? = null
    var minZ: Float? = null
    var maxX: Float? = null
    var maxY: Float? = null
    var maxZ: Float? = null
    var minLight: Int? = null
    var maxLight: Int? = null
    var minSkyLight: Int? = null
    var maxSkyLight: Int? = null
    var isRaining: Boolean? = null
    var isThundering: Boolean? = null
    var timeRange: TimeRange? = null
    var structures: List<Identifier>? = null

    override fun encode(buffer: PacketByteBuf) {

        val dimensionList = dimensions ?: listOf()
        buffer.writeCollection(dimensionList) {
            pbb, value -> pbb.writeIdentifier(value)
        }

        val biomeList = biomes?.mapNotNull { biome ->
            if (biome is RegistryLikeTagCondition<Biome>)
                biome.tag.id
            else null
        } ?: listOf()

        buffer.writeCollection(biomeList) {
            pbb, value -> pbb.writeIdentifier(value)
        }

        val moonPhaseRanges = moonPhase ?: mutableListOf()
        buffer.writeCollection(moonPhaseRanges) {
            pbb, value -> pbb.writeIntRange(value)
        }

        buffer.writeNullableBool(canSeeSky)
        buffer.writeNullableFloat(minX)
        buffer.writeNullableFloat(minY)
        buffer.writeNullableFloat(minZ)
        buffer.writeNullableFloat(maxX)
        buffer.writeNullableFloat(maxY)
        buffer.writeNullableFloat(maxZ)
        buffer.writeNullableInt(minLight)
        buffer.writeNullableInt(maxLight)
        buffer.writeNullableInt(minSkyLight)
        buffer.writeNullableInt(maxSkyLight)
        buffer.writeNullableBool(isRaining)
        buffer.writeNullableBool(isThundering)

        val timeRanges = timeRange?.ranges ?: mutableListOf()
        buffer.writeCollection(timeRanges) {
            pbb, value -> pbb.writeIntRange(value)
        }

        val structureList = structures ?: listOf()

        buffer.writeCollection(structureList) {
            pbb, value -> pbb.writeIdentifier(value)
        }
    }

    companion object {
        fun decode(buffer: PacketByteBuf) : SerializableSpawnCondition {
            val value = SerializableSpawnCondition()


            value.dimensions = buffer.readList{  reader -> reader.readIdentifier() }
            value.biomes = buffer.readList { reader ->
                val tag = TagKey.of(RegistryKeys.BIOME, reader.readIdentifier())
                RegistryLikeTagCondition<Biome>(tag)
            }.toMutableSet()

            value.moonPhase = buffer.readList { reader ->
                reader.readIntRange()
            }

            value.canSeeSky = buffer.readNullableBool()
            value.minX = buffer.readNullableFloat()
            value.minY = buffer.readNullableFloat()
            value.minZ = buffer.readNullableFloat()
            value.maxX = buffer.readNullableFloat()
            value.maxY = buffer.readNullableFloat()
            value.maxZ = buffer.readNullableFloat()
            value.minLight = buffer.readNullableInt()
            value.maxLight = buffer.readNullableInt()
            value.minSkyLight = buffer.readNullableInt()
            value.maxSkyLight = buffer.readNullableInt()
            value.isRaining = buffer.readNullableBool()
            value.isThundering = buffer.readNullableBool()

            value.timeRange = TimeRange()
            value.timeRange?.ranges = buffer.readList { reader ->
                reader.readIntRange()
            }

            value.structures = buffer.readList { reader ->
                reader.readIdentifier()
            }

            return value
        }
    }
}