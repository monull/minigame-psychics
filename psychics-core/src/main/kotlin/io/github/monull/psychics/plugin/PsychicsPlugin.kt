package io.github.monull.psychics.plugin

import io.github.monull.psychics.PsychicManager
import io.github.monull.psychics.Psychics
import io.github.monull.psychics.attribute.EsperStatistic
import io.github.monull.psychics.damage.Damage
import io.github.monun.tap.event.EntityEventManager
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PsychicsPlugin : JavaPlugin() {
    lateinit var fakeEntityServer: FakeEntityServer
        private set

    lateinit var entityEventManager: EntityEventManager
        private set

    lateinit var psychicManager: PsychicManager
        private set

    override fun onLoad() {
        ConfigurationSerialization.registerClass(Damage::class.java)
        ConfigurationSerialization.registerClass(EsperStatistic::class.java)
    }

    override fun onEnable() {
        loadModules()
        registerPlayers()
        Psychics.initialize(this, logger, psychicManager, fakeEntityServer)
    }

    private fun loadModules() {
        fakeEntityServer = FakeEntityServer.create(this).apply {
            Bukkit.getOnlinePlayers().forEach { addPlayer(it) }
        }
        entityEventManager = EntityEventManager(this)
        psychicManager = PsychicManager(
            this,
            logger,
            File(dataFolder, "abilities"),
            File(dataFolder, "espers")
        )

        psychicManager.run {
            updateAbilities()
            loadAbilities()
            loadPsychic()
        }

        server.apply {
            pluginManager.registerEvents(
                EventListener(
                    psychicManager,
                    fakeEntityServer
                ), this@PsychicsPlugin
            )
            scheduler.runTaskTimer(this@PsychicsPlugin, SchedulerTask(psychicManager, fakeEntityServer), 0L, 1L)
        }
    }

    private fun registerPlayers() {
        for (player in server.onlinePlayers) {
            fakeEntityServer.addPlayer(player)
            psychicManager.addPlayer(player)
        }
    }

    override fun onDisable() {
        if (this::psychicManager.isInitialized) {
            psychicManager.unload()
        }
        if (this::fakeEntityServer.isInitialized) {
            fakeEntityServer.clear()
        }
    }

    fun reloadPsychics() {
        fakeEntityServer.entities.forEach { it.remove() }
        entityEventManager.unregisterAll()
        psychicManager.reload()
    }
}