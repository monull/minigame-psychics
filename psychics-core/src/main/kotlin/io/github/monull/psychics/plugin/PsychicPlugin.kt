package io.github.monull.psychics.plugin

import io.github.monull.psychics.PsychicManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PsychicPlugin : JavaPlugin() {
    override fun onEnable() {
        val manager = PsychicManager(logger, File(dataFolder, "abilities")).apply {
            loadAbilities()
        }
    }
}