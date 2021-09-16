package io.github.monull.psychics

import io.github.monull.psychics.plugin.PsychicsPlugin
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.entity.Player
import java.util.logging.Logger

object Psychics {
    lateinit var plugin: PsychicsPlugin
        private set

    lateinit var logger: Logger
        private set

    lateinit var psychicManager: PsychicManager
        private set

    lateinit var fakeEntityServer: FakeEntityServer
        private set

    internal fun initialize(
        plugin: PsychicsPlugin,
        logger: Logger,
        psychicManager: PsychicManager,
        fakeEntityServer: FakeEntityServer
    ) {
        this.plugin = plugin
        this.logger = logger
        this.psychicManager = psychicManager
        this.fakeEntityServer = fakeEntityServer
    }
}

val Player.esper: Esper
    get() = requireNotNull(Psychics.psychicManager.getEsper(this)) { "Not found esper for $this" }