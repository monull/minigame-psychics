package io.github.monull.psychics

import org.bukkit.configuration.ConfigurationSection

open class Ability<T : AbilityConcept> {
    lateinit var concept: T
        private set

    internal fun initConcept(concept: AbilityConcept) {
        this.concept = concept as T
    }

    open fun onEnable() {}

    open fun onDisable() {}

    open fun onSave(config: ConfigurationSection) {}

    open fun onLoad(config: ConfigurationSection) {}
}