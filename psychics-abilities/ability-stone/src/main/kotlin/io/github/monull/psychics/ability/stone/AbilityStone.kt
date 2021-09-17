package io.github.monull.psychics.ability.stone

import io.github.monull.psychics.AbilityConcept
import io.github.monull.psychics.ActiveAbility
import io.github.monull.psychics.attribute.EsperAttribute
import io.github.monull.psychics.damage.Damage
import io.github.monull.psychics.damage.DamageType
import io.github.monun.tap.config.Config
import net.kyori.adventure.text.Component.text
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

class AbilityConceptStone : AbilityConcept() {
    @Config
    var stoneMaxDistance = 30.0

    init {
        damage = Damage.of(DamageType.RANGED, EsperAttribute.ATTACK_DAMAGE to 2.0)
        knockback = 1.0
        description = listOf(
            text("")
        )
        wand = ItemStack(Material.STONE_SWORD)
    }
}
class AbilityStone : ActiveAbility<AbilityConceptStone>() {

    override fun onCast(event: PlayerEvent, action: WandAction, target: Any?) {
        val start = esper.player.eyeLocation
        val direction = start.direction
        val maxDistance = concept.stoneMaxDistance

        esper.player.world.rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, true)?.let { result ->
            val blockLoc = result.hitBlock?.location!!.apply { y += 1 }
            psychic.runTaskTimer(StoneScheduler(blockLoc), 0L, 1L)
        }
    }

    inner class StoneScheduler(val targetLoc: Location) : Runnable {
        private var ticks = 0
        override fun run() {
            ticks++
            when (ticks) {
                1 -> {
                    targetLoc.block.run {
                        type = Material.MOSSY_COBBLESTONE
                        getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                        getRelative(1.0, 0.0, 0.0).type = Material.COBBLESTONE_SLAB
                    }
                }
                2 -> {
                    targetLoc.block.run {
                        type = Material.COBBLESTONE
                        getRelative(0.0, 1.0, 0.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(1.0, 0.0, 0.0).type = Material.COBBLESTONE_SLAB
                            getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                        }
                        getRelative(0.0, 0.0, -1.0).run {
                            type = Material.COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            getRelative(1.0, 0.0, 0.0).type = Material.TUFF
                        }
                        getRelative(-1.0, 0.0, 0.0).type = Material.COBBLESTONE
                        getRelative(1.0, 0.0, 0.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                        }
                        getRelative(0.0, 0.0, 1.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(1.0, 0.0, 0.0).type = Material.MOSSY_COBBLESTONE
                        }
                    }
                }
                3 -> {
                    targetLoc.block.run {
                        type = Material.MOSSY_COBBLESTONE
                        getRelative(0.0, 0.0, -1.0).run {
                            type = Material.COBBLESTONE
                            getRelative(1.0, 0.0, 0.0).type = Material.COBBLESTONE
                            getRelative(-1.0, 0.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.TUFF
                            }
                            getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                        }
                        getRelative(0.0, 1.0, 0.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            }
                        }
                        getRelative(-1.0, 0.0, 0.0).run {
                            type = Material.COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                        }
                        getRelative(1.0, 0.0, 0.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                        }
                        getRelative(0.0, 0.0, 1.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                            getRelative(1.0, 0.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            }
                        }
                    }
                }
                4 -> {
                    targetLoc.block.run {
                        type = Material.COBBLESTONE
                        getRelative(0.0, 0.0, -1.0).run {
                            type = Material.COBBLESTONE
                            getRelative(1.0, 0.0, 0.0).type = Material.COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                            }
                            getRelative(-1.0, 0.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, -1.0, 0.0).type = Material.TUFF
                            }
                        }
                        getRelative(0.0, 1.0, 0.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                            }
                        }
                        getRelative(1.0, 0.0, 0.0).run {
                            type = Material.COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            }
                        }
                        getRelative(-1.0, 0.0, 0.0).run {
                            type = Material.COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            }
                        }
                        getRelative(0.0, 0.0, 1.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            }
                            getRelative(-1.0, 0.0, 0.0).type = Material.TUFF
                            getRelative(1.0, 0.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            }
                        }
                    }
                }
                5 -> {
                    targetLoc.block.run {
                        type = Material.COBBLESTONE
                        getRelative(0.0, 0.0, -1.0).run {
                            type = Material.COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).run {
                                    type = Material.COBBLESTONE
                                    getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                                }
                            }
                            getRelative(1.0, 0.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                            }
                            getRelative(-1.0, 0.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.TUFF
                            }
                        }
                        getRelative(0.0, 1.0, 0.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).run {
                                    type = Material.MOSSY_COBBLESTONE
                                    getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                                }
                            }
                        }
                        getRelative(0.0, 0.0, 1.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                            }
                            getRelative(-1.0, 0.0, 0.0).type = Material.TUFF
                            getRelative(1.0, 0.0, 0.0).run {
                                type = Material.TUFF
                                getRelative(0.0, 1.0, 0.0).run {
                                    type = Material.MOSSY_COBBLESTONE
                                    getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE_SLAB
                                }
                            }
                        }
                        getRelative(-1.0, 0.0, 0.0).run {
                            type = Material.COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                            }
                        }
                        getRelative(1.0, 0.0, 0.0).run {
                            type = Material.MOSSY_COBBLESTONE
                            getRelative(0.0, 1.0, 0.0).run {
                                type = Material.MOSSY_COBBLESTONE
                                getRelative(0.0, 1.0, 0.0).type = Material.MOSSY_COBBLESTONE
                            }
                        }
                    }
                }
            }
        }
    }

    fun Block.getRelative(x: Double, y: Double, z: Double): Block {
        val location = Location(esper.player.world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
        location.add(x, y, z)
        return location.block
    }

    fun Location.getRelative(x: Double, y: Double, z: Double): Block {
        add(x, y, z)
        return block
    }
}