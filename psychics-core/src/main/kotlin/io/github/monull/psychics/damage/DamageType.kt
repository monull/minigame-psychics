package io.github.monull.psychics.damage

import org.bukkit.enchantments.Enchantment

enum class DamageType(
    val protection: Enchantment,
    val i18Name: String
) {
    MELEE(Enchantment.PROTECTION_ENVIRONMENTAL, "근접 피해"), //보호
    RANGED(Enchantment.PROTECTION_PROJECTILE, "원거리 피해"), //원거리 피해
    FIRE(Enchantment.PROTECTION_FIRE, "화염 피해"), //화염 보호
    BLAST(Enchantment.PROTECTION_EXPLOSIONS, "폭발 피해"), // 폭발 보호
}