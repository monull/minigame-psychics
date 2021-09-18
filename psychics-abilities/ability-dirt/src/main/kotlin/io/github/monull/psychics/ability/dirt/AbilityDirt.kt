package io.github.monull.psychics.ability.dirt

import io.github.monull.psychics.AbilityConcept
import io.github.monull.psychics.ActiveAbility
import io.github.monull.psychics.PsychicProjectile
import io.github.monull.psychics.attribute.EsperAttribute
import io.github.monull.psychics.damage.Damage
import io.github.monull.psychics.damage.DamageType
import io.github.monull.psychics.util.TargetFilter
import io.github.monun.tap.config.Config
import io.github.monun.tap.config.Name
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.fake.Movement
import io.github.monun.tap.fake.Trail
import io.github.monun.tap.math.normalizeAndLength
import net.kyori.adventure.text.Component.text
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

@Name("dirt")
class AbilityConceptDirt : AbilityConcept() {
    @Config
    var gravity = 0.04

    @Config
    var dirtSpeed = 2

    @Config
    var acceleration = 0.01

    init {
        cooldownTime = 20
        damage = Damage.of(DamageType.RANGED, EsperAttribute.ATTACK_DAMAGE to 0.5)
        knockback = 1.0
        description = listOf(
            text("스킬 사용시 흙 발사")
        )
        wand = ItemStack(Material.WOODEN_SHOVEL)
    }
}

class AbilityDirt : ActiveAbility<AbilityConceptDirt>() {

    override fun onCast(event: PlayerEvent, action: WandAction, target: Any?) {
        cooldownTime = concept.cooldownTime
        val projectile = DirtProjectile()
        projectile.dirt = psychic.spawnFakeEntity(esper.player.eyeLocation, ArmorStand::class.java).apply {
            updateMetadata<ArmorStand> {
                isInvisible = true
                isMarker = true
            }
            updateEquipment {
                helmet = ItemStack(Material.DIRT)
            }
        }
        projectile.velocity = esper.player.eyeLocation.direction.multiply(concept.dirtSpeed)
        psychic.launchProjectile(esper.player.eyeLocation, projectile)
    }

    inner class DirtProjectile() : PsychicProjectile(140, 70.0) {
        var dirt: FakeEntity? = null

        override fun onPreUpdate() {
            velocity = velocity.apply { y -= concept.gravity }
        }

        override fun onMove(movement: Movement) {
            val to = movement.to
            dirt?.let { dirt ->
                dirt.moveTo(to)
            }
        }

        override fun onTrail(trail: Trail) {
            trail.velocity?.let { v ->
                val from = trail.from
                val world = from.world

                val length = v.normalizeAndLength()
                if (length == 0.0) return

                val filter = TargetFilter(esper.player)

                world.rayTrace(
                    from,
                    v,
                    length,
                    FluidCollisionMode.NEVER,
                    true,
                    1.0,
                    filter,

                )?.let { rayTraceResult ->

                    remove()

                    if(rayTraceResult.hitEntity == null) return

                    (rayTraceResult.hitEntity as LivingEntity).psychicDamage()
                }
            }
        }

        override fun onRemove() {
            dirt?.remove()
        }
    }
}