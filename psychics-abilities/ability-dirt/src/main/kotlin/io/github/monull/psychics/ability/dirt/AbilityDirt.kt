package io.github.monull.psychics.ability.dirt

import io.github.monull.psychics.Ability
import io.github.monull.psychics.AbilityConcept

class AbilityConceptDirt: AbilityConcept() {
    init {
        displayName = "흙"
        cooldownTime = 15
    }
}

class AbilityDirt : Ability<AbilityConceptDirt>() {
    override fun onEnable() {

    }
}