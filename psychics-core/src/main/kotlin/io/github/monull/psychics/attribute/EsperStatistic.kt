package io.github.monull.psychics.attribute

import io.github.monull.psychics.format.decimalFormat
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.md_5.bungee.api.ChatColor
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.*

class EsperStatistic internal constructor(pairs: List<Pair<EsperAttribute, Double>>) : ConfigurationSerializable {
    internal val stats: Map<EsperAttribute, Double>

    init {
        val stats = EnumMap<EsperAttribute, Double>(EsperAttribute::class.java)
        for (pair in pairs) {
            stats[pair.first] = pair.second
        }
        this.stats = stats
    }

    override fun toString(): String {
        val builder = StringBuilder()

        for ((attr, ratio) in stats) {
            builder
                .append(attr.color)
                .append('(').append(ratio.decimalFormat()).append(' ')
                .append(attr.abbr).append(')')
                .append(ChatColor.RESET)
        }

        return builder.toString()
    }

    fun toComponent(): Component {
        val builder = text()

        for ((attr, ratio) in stats) {
            builder.append(text().color(attr.color).content("(${ratio.decimalFormat()}${attr.abbr})"))
        }

        return builder.build()
    }

    override fun serialize(): Map<String, Any> {
        return stats.mapKeys { it.key.abbr }
    }

    companion object {

        @JvmStatic
        fun deserialize(map: Map<String, *>): EsperStatistic {
            val list = ArrayList<Pair<EsperAttribute, Double>>()

            for ((abbr, value) in map) {
                val attribute = requireNotNull(EsperAttribute.byAbbr[abbr])
                val ratio = requireNotNull(value) as Double

                list += attribute to ratio
            }

            return EsperStatistic(list)
        }

        fun of(stat: Pair<EsperAttribute, Double>): EsperStatistic {
            return construct(stat)
        }

        fun of(
            s1: Pair<EsperAttribute, Double>,
            s2: Pair<EsperAttribute, Double>
        ): EsperStatistic {
            return construct(s1, s2)
        }

        fun of(
            s1: Pair<EsperAttribute, Double>,
            s2: Pair<EsperAttribute, Double>,
            s3: Pair<EsperAttribute, Double>
        ): EsperStatistic {
            return construct(s1, s2, s3)
        }

        fun of(
            s1: Pair<EsperAttribute, Double>,
            s2: Pair<EsperAttribute, Double>,
            s3: Pair<EsperAttribute, Double>,
            s4: Pair<EsperAttribute, Double>
        ): EsperStatistic {
            return construct(s1, s2, s3, s4)
        }

        fun of(
            s1: Pair<EsperAttribute, Double>,
            s2: Pair<EsperAttribute, Double>,
            s3: Pair<EsperAttribute, Double>,
            s4: Pair<EsperAttribute, Double>,
            s5: Pair<EsperAttribute, Double>
        ): EsperStatistic {
            return construct(s1, s2, s3, s4, s5)
        }

        private fun construct(vararg stats: Pair<EsperAttribute, Double>): EsperStatistic {
            return EsperStatistic(stats.asList())
        }
    }

}