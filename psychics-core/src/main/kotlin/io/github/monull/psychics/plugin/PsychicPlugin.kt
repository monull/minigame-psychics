package io.github.monull.psychics.plugin

import io.github.monull.psychics.PsychicManager
import io.github.monun.kommand.kommand
import io.github.monun.tap.event.EntityEventManager
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PsychicPlugin : JavaPlugin() {

    lateinit var fakeEntityServer: FakeEntityServer
        private set

    lateinit var entityEventManager: EntityEventManager
        private set

    lateinit var psychicManager: PsychicManager
        private set

    override fun onEnable() {
        loadModules()
        registerPlayers()
        registerKommand()
    }

    private fun loadModules() {
        fakeEntityServer = FakeEntityServer.create(this).apply {
            Bukkit.getOnlinePlayers().forEach { addPlayer(it) }
        }
        entityEventManager = EntityEventManager(this)
        psychicManager = PsychicManager(this, logger, File(dataFolder, "abilities")).apply {
            loadAbilities()
        }

        server.apply {
            pluginManager.registerEvents(
                EventListener(
                    psychicManager,
                    fakeEntityServer
                ), this@PsychicPlugin
            )
            scheduler.runTaskTimer(this@PsychicPlugin, SchedulerTask(psychicManager, fakeEntityServer), 0L, 1L)
        }
    }

    private fun registerPlayers() {
        for (player in server.onlinePlayers) {
            fakeEntityServer.addPlayer(player)
            psychicManager.addPlayer(player)
        }
    }

    private fun registerKommand() {
        kommand {
            register("test") {
                requires { playerOrNull != null }
                executes {
                    psychicManager.getEsper(sender as Player)?.psychic?.abilities?.forEach { it ->
                        it.runCatching {
                            onEnable()
                        }.onFailure {
                            it.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}