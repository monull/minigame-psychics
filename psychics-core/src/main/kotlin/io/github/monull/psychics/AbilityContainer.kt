package io.github.monull.psychics

import java.io.File

class AbilityContainer(
    val file: File,
    val description: AbilityDescription,
    val conceptClass: Class<out AbilityConcept>,
    val abilityClass: Class<out Ability<*>>
)