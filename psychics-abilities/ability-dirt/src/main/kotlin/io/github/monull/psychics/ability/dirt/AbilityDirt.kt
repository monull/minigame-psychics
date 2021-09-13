package io.github.monull.psychics.ability.dirt

import io.github.monull.psychics.Ability
import io.github.monull.psychics.AbilityConcept
import org.bukkit.Location
import org.bukkit.entity.ArmorStand

class AbilityConceptDirt: AbilityConcept() {
    init {
        displayName = "흙"
        cooldownTime = 15
    }
}

class AbilityDirt : Ability<AbilityConceptDirt>() {
    override fun onEnable() {
        val loc = Location(esper.player.world, 0.0, 10.0, 0.0)
        psychic.spawnFakeEntity(loc, ArmorStand::class.java)
    }
}