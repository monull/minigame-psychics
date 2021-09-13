package io.github.monull.psychics

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

open class Ability<T : AbilityConcept> {
    lateinit var concept: T
        private set

    val esper
        get() = psychic.esper

    lateinit var psychic: Psychic
        private set

    internal fun initPsychic(psychic: Psychic) {
        this.psychic = psychic
    }

    internal fun initConcept(concept: AbilityConcept) {
        this.concept = concept as T
    }

    open fun onInitialize() {}

    open fun onEnable() {}

    open fun onDisable() {}

    open fun onSave(config: ConfigurationSection) {}

    open fun onLoad(config: ConfigurationSection) {}
}