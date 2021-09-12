package io.github.monull.psychics

import net.kyori.adventure.text.format.NamedTextColor


enum class AbilityType(
    val color: NamedTextColor
) {
    PASSIVE(NamedTextColor.AQUA),
    ACTIVE(NamedTextColor.RED),
    TOGGLE(NamedTextColor.YELLOW),
    COMPLEX(NamedTextColor.WHITE)
}