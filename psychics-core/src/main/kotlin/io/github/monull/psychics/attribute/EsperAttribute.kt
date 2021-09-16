package io.github.monull.psychics.attribute

import com.google.common.collect.ImmutableSortedMap
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*

enum class EsperAttribute(
    val abbr: String,
    val i18DisplayName: String,
    val color: NamedTextColor
) {
    ATTACK_DAMAGE("ATK", "공격력", NamedTextColor.GOLD),
    LEVEL("LVL", "레벨", NamedTextColor.GREEN),
    DEFENSE("DEF", "방어", NamedTextColor.WHITE),
    HEALTH("HP", "체력", NamedTextColor.RED);

    companion object {
        val byAbbr: Map<String, EsperAttribute>

        init {
            val map = TreeMap<String, EsperAttribute>(String.CASE_INSENSITIVE_ORDER)

            for (value in values()) {
                map[value.abbr] = value
            }

            byAbbr = ImmutableSortedMap.copyOf(map)
        }
    }
}