package io.github.monull.psychic.ability.diamond

import io.github.monull.psychics.Ability
import io.github.monull.psychics.AbilityConcept
import io.github.monull.psychics.attribute.EsperAttribute
import io.github.monull.psychics.attribute.EsperStatistic
import io.github.monull.psychics.damage.Damage
import io.github.monull.psychics.damage.DamageType
import io.github.monull.psychics.tooltip.TooltipBuilder
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
        knockback = 1.0
        description = listOf(
            text("허공에 다이아검을 휘두를 시 멀리 있는 상대 공격")
        )
    }

    override fun onRenderTooltip(tooltip: TooltipBuilder, stats: (EsperStatistic) -> Double) {
        tooltip.header(
            text().content("최대 사거리 ").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false)
                .append(text().content(diamondDistance.toInt().toString())).build()
        )
    }
}

class AbilityDiamond : Ability<AbilityConceptDiamond>(), Runnable, Listener {
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
        if (esper.player.inventory.contains(ItemStack(Material.DIAMOND_SWORD))) {
            esper.player.inventory.run {
                val iterator = iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()

                    if (next.type == Material.DIAMOND_SWORD) {
                        next.itemMeta = next.itemMeta.apply {
                            val lore = lore() ?: ArrayList<Component>()
                            lore.add(0, text().content("허공에 휘두를 때: ").decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.GRAY).build())
                            lore.add(1, text().content("${concept.diamondDistance.toInt()} 공격 거리").decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.DARK_GREEN).build())
                            lore(lore)
                        }
                    }
                }
            }
        }
    }
}