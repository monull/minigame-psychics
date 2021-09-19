package io.github.monull.psychics.ability.iron

import io.github.monull.psychics.AbilityConcept
import io.github.monull.psychics.ActiveAbility
import io.github.monun.tap.config.Config
import io.github.monun.tap.config.Name
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerVelocityEvent
import org.bukkit.inventory.ItemStack

@Name("iron")
class AbilityConceptIron : AbilityConcept() {
    @Config
    var damageOnCasting = 0.5

    init {
        cooldownTime = 5000
        durationTime = 5000
        wand = ItemStack(Material.IRON_SWORD)
    }
}

class AbilityIron : ActiveAbility<AbilityConceptIron>(), Listener {
    override fun onEnable() {
        psychic.registerEvents(this)
    }
    override fun onCast(event: PlayerEvent, action: WandAction, target: Any?) {
        cooldownTime = concept.cooldownTime
        durationTime = concept.durationTime
    }

    @EventHandler
    fun onPlayerDamaged(event: EntityDamageEvent) {
        if (durationTime > 0) {
            event.damage = event.damage * concept.damageOnCasting
        }
    }

    @EventHandler
    fun onPlayerVelocity(event: PlayerVelocityEvent) {
        if (durationTime > 0) {
            event.isCancelled = true
        }
    }
}