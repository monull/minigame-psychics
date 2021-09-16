package io.github.monull.psychics.plugin

import io.github.monull.psychics.ActiveAbility
import io.github.monull.psychics.Psychic
import io.github.monull.psychics.PsychicManager
import io.github.monull.psychics.esper
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class EventListener(
    private val psychicManager: PsychicManager,
    private val fakeEntityServer: FakeEntityServer
) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        psychicManager.addPlayer(player)
        fakeEntityServer.addPlayer(player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        psychicManager.removePlayer(player)
        fakeEntityServer.removePlayer(player)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val action = event.action
        val hand = event.hand
        val item = event.item

        if (action != Action.PHYSICAL && hand == EquipmentSlot.HAND && item != null) {
            val player = event.player
            val wandAction =
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) ActiveAbility.WandAction.LEFT_CLICK
                else ActiveAbility.WandAction.RIGHT_CLICK

            player.esper.psychic?.castByWand(event, wandAction, item)
        }
    }

    @EventHandler
    fun onInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        if (item.type != Material.AIR) {
            player.esper.psychic?.castByWand(event, ActiveAbility.WandAction.RIGHT_CLICK, item)
        }
    }
}

private fun Psychic.castByWand(event: PlayerEvent, action: ActiveAbility.WandAction, item: ItemStack) {
    esper.psychic?.let { psychic ->
        val ability = psychic.getAbilityByWand(item)

        if (ability is ActiveAbility) {
            ability.tryCast(event, action)
        }
    }
}