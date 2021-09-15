package io.github.monull.psychics.plugin

import io.github.monull.psychics.PsychicManager
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

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
}