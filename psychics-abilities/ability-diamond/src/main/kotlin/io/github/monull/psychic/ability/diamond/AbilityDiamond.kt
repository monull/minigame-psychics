package io.github.monull.psychic.ability.diamond

import io.github.monull.psychics.Ability
import io.github.monull.psychics.AbilityConcept
import io.github.monull.psychics.attribute.EsperAttribute
import io.github.monull.psychics.damage.Damage
import io.github.monull.psychics.damage.DamageType
import io.github.monull.psychics.util.TargetFilter
import io.github.monun.tap.config.Config
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class AbilityConceptDiamond : AbilityConcept() {
    @Config
    var diamondDistance = 40.0

    init {
        wand = ItemStack(Material.DIAMOND_SWORD)
        damage = Damage.of(DamageType.RANGED, EsperAttribute.ATTACK_DAMAGE to 3.5)
        description = listOf(
            text("허공에 다이아검 휘두를 시 멀리 있는 상대 공격"),
            text("최대 사거리 $diamondDistance")
        )
    }
}

class AbilityDiamond : Ability<AbilityConceptDiamond>(), Listener, Runnable {
    val rangeTag =
        text().content("허공에 휘두를 때:").decoration(TextDecoration.ITALIC, false)
            .color(NamedTextColor.GRAY).build()
    val diamondRange = text().content("${concept.diamondDistance} 공격 거리").decoration(TextDecoration.ITALIC, false)
        .color(NamedTextColor.DARK_GREEN).build()
    val diamondSword = ItemStack(Material.DIAMOND_SWORD).apply {
        itemMeta = itemMeta.apply {
            val lore = lore() ?: ArrayList<Component>()
            lore.add(0, rangeTag)
            lore.add(1, diamondRange)
            lore(lore)
        }
        durability = 1461
    }
    override fun onEnable() {
        psychic.registerEvents(this)
        psychic.runTaskTimer(this, 0L, 1L)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.LEFT_CLICK_AIR && event.player.itemInHand.type == concept.wand?.type) {
            val loc = esper.player.eyeLocation
            val direction = loc.direction
            val maxDistance = concept.diamondDistance
            val filter = TargetFilter(esper.player)
            esper.player.world.rayTrace(loc, direction, maxDistance, FluidCollisionMode.NEVER, true, 1.0, filter)?.let { result ->
                if (result.hitEntity != null && result.hitEntity is LivingEntity) {
                    result.hitEntity as LivingEntity
                    (result.hitEntity as LivingEntity).psychicDamage()
                }
            }
        }
    }

    override fun run() {
        esper.player.inventory.run {
            val iterator = iterator()
            while (iterator.hasNext()) {
                var next = iterator.next()
                if (next.type == Material.DIAMOND_SWORD) {
                    next = diamondSword
                }
            }
        }
    }
}