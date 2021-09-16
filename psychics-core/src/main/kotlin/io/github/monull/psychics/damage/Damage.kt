package io.github.monull.psychics.damage

import io.github.monull.psychics.attribute.EsperAttribute
import io.github.monull.psychics.attribute.EsperStatistic
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable

class Damage internal constructor(
    val type: DamageType,
    val stats: EsperStatistic
) : ConfigurationSerializable {
    override fun toString(): String {
        return "$stats${ChatColor.BOLD}${type.i18Name}"
    }

    override fun serialize(): Map<String, Any> {
        return mapOf(
            TYPE to type.name,
            STATS to stats.serialize()
        )
    }

    companion object {
        private const val TYPE = "type"
        private const val STATS = "stats"

        fun of(type: DamageType, stats: EsperStatistic) = Damage(type, stats)

        fun of(type: DamageType, stats: Pair<EsperAttribute, Double>) = of(type, EsperStatistic.of(stats))

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun deserialize(map: Map<String, *>): Damage {
            val type = DamageType.valueOf(requireNotNull(map[TYPE]) as String)

            val statsValue = requireNotNull((map[STATS] as ConfigurationSection).getValues(false))
            val stats = EsperStatistic.deserialize(statsValue)

            return Damage(type, stats)
        }
    }
}