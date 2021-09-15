package io.github.monull.psychics

import io.github.monun.tap.config.Config
import io.github.monun.tap.config.ConfigSupport
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

class PsychicConcept internal constructor() {

    lateinit var manager: PsychicManager
        private set

    lateinit var name: String
        private set

    /**
     * 표시 이름 (I18N)
     */
    @Config
    lateinit var displayName: String
        private set
    
    @Config("description")
    private var descriptionRaw: List<String> = ArrayList(0)

    private var description: List<Component> = listOf(
        text("설명")
    )

    /**
     * 능력 목록
     */
    lateinit var abilityConcepts: List<AbilityConcept>
        private set


    internal fun initialize(name: String, config: ConfigurationSection): Boolean {
        this.name = name
        this.displayName = name

        val gson = GsonComponentSerializer.gson()
        descriptionRaw = description.map { gson.serialize(it) }

        val ret = ConfigSupport.compute(this, config)

        return ret
    }

    internal fun initializeModules(manager: PsychicManager, abilityConcepts: List<AbilityConcept>) {
        this.manager = manager
        this.abilityConcepts = abilityConcepts

        for (abilityConcept in abilityConcepts) {
            abilityConcept.runCatching {
                onInitialize()
            }
        }
    }

    internal fun createInstance(): Psychic {
        val manager = manager

        return Psychic(this).apply {
            this.initialize(manager.plugin, manager)
        }
    }
}