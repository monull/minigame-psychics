package io.github.monull.psychics.ability.stone

import io.github.monull.psychics.AbilityConcept
import io.github.monull.psychics.ActiveAbility
import io.github.monun.tap.config.Config
import io.github.monun.tap.task.TickerTask
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
        cooldownTime = 60
        durationTime = 60
        description = listOf(
            text("돌 검 사용시 돌 탑 소환"),
            text("돌 탑 근처에 있던 사람은 가벼운 넉백으로 공중으로 날아감")
        )
        wand = ItemStack(Material.STONE_SWORD)
    }
}
class AbilityStone : ActiveAbility<AbilityConceptStone>() {

    override fun onCast(event: PlayerEvent, action: WandAction, target: Any?) {
        cooldownTime = concept.cooldownTime
        val start = esper.player.eyeLocation
        val direction = start.direction
        val maxDistance = concept.stoneMaxDistance

        esper.player.world.rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, true)?.let { result ->
            val blockLoc = result.hitBlock?.location!!.apply { y += 1 }
            val scheduler = StoneScheduler(blockLoc)
            scheduler.task = psychic.runTaskTimer(scheduler, 0L, 1L)
        }
    }

    inner class StoneScheduler(val targetLoc: Location) : Runnable {
        private var ticks = 0
        lateinit var task: TickerTask

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
                concept.durationTime.toInt() -> {
                    targetLoc.block.run {
                        reset(5)
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
                concept.durationTime.toInt() + 1 -> {
                    targetLoc.block.run {
                        reset(5)
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
                concept.durationTime.toInt() + 2 -> {
                    targetLoc.block.run {
                        reset(4)
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
                concept.durationTime.toInt() + 3 -> {
                    targetLoc.block.run {
                        reset(3)
                        type = Material.MOSSY_COBBLESTONE
                        getRelative(0.0, 1.0, 0.0).type = Material.COBBLESTONE
                        getRelative(1.0, 0.0, 0.0).type = Material.COBBLESTONE_SLAB
                    }
                }
                concept.durationTime.toInt() + 4 -> {
                    targetLoc.block.reset(2)
                    task.cancel()
                }
            }
        }
    }

    fun Block.reset(height: Int) {
        val x = location.x.toInt()
        val y = location.y.toInt()
        val z = location.z.toInt()
        for (x in (x - 1)..(x + 1)) {
            for ( y in y until y + height) {
                for (z in (z - 1)..(z + 1)) {
                    val loc = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                    loc.block.type = Material.AIR
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