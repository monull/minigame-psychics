package io.github.monull.psychics.ability.dirt

import io.github.monull.psychics.Ability
import io.github.monull.psychics.AbilityConcept
import io.github.monun.tap.config.Name
import net.kyori.adventure.text.Component.text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

@Name("dirt")
class AbilityConceptDirt : AbilityConcept() {
    init {
        description = listOf(
            text("스킬 사용시 흙 발사")
        )
    }
}

class AbilityDirt : Ability<AbilityConceptDirt>(), Listener {
    override fun onEnable() {
        psychic.registerEvents(this)
    }

    @EventHandler
    fun onPlayerToggleSneak(event: PlayerToggleSneakEvent) {
        psychic.broadcast("Hello Ability!")
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        event.player.sendMessage("능력자가 손을 휘둘렀다.")
    }
}