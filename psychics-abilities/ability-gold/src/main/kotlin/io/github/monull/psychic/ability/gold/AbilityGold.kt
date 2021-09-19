package io.github.monull.psychic.ability.gold

import io.github.monull.psychics.Ability
import io.github.monull.psychics.AbilityConcept
import io.github.monull.psychics.attribute.EsperAttribute
import io.github.monull.psychics.attribute.EsperStatistic
import io.github.monun.tap.config.Name
import io.github.monun.tap.event.EntityProvider
import io.github.monun.tap.event.TargetEntity
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

@Name("gold")
class AbilityConceptGold : AbilityConcept() {
    init {
        wand = ItemStack(Material.GOLDEN_SWORD)
        description = listOf(
            text("금 검으로 상대를 공격할 시 흡혈"),
            text("흡혈 시 피 회복")
        )
        healing = EsperStatistic.of(EsperAttribute.ATTACK_DAMAGE to 2.0)
    }
}

class AbilityGold : Ability<AbilityConceptGold>(), Listener {
    override fun onEnable() {
        psychic.registerEvents(this)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @TargetEntity(EntityProvider.EntityDamageByEntity.Damager::class)
    fun onEntityAttack(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && (event.damager as Player).itemInHand.type == concept.wand?.type) {
            (event.damager as Player).psychicHeal()
        }
    }
}