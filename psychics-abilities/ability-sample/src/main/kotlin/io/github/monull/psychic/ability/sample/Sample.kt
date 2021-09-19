package io.github.monull.psychic.ability.sample

import io.github.monull.psychics.Ability
import io.github.monull.psychics.AbilityConcept
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent

class SampleConcept: AbilityConcept() {
    init {
        displayName = "Ability예제"
        cooldownTime = 10000 // 10초
    }
}

class Sample: Ability<SampleConcept>(), Listener, Runnable {
    override fun onEnable() {
        psychic.registerEvents(this)
        psychic.runTaskTimer(this, 0L, 1L)
    }

    @EventHandler
    fun onPlayerSneak(event: PlayerToggleSneakEvent) {
        event.player.sendMessage("Sneak event")
    }

    override fun run() {
        esper.player.sendMessage("Task running")
    }
}