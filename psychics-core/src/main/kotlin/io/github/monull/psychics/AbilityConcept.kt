package io.github.monull.psychics

import io.github.monun.tap.config.*
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

@Name("common")
open class AbilityConcept {
    lateinit var name: String
        private set

    lateinit var container: AbilityContainer

    @Config
    lateinit var displayName: String
        protected set

    @Config(required = false)
    @RangeInt(min = 0)
    var cooldownTime = 0L
        protected set

    @Config(required = false)
    var durationTime = 0L
        protected set

    @Config(required = false)
    @RangeDouble(min = 0.0)
    var range = 0.0
        protected set

    @Config(required = false)
    var knockback = 0.0

    @Config("description")
    private var descriptionRaw: List<String> = ArrayList(0)

    var description: List<Component> = emptyList()

    internal fun initialize(
        name: String,
        container: AbilityContainer,
        config: ConfigurationSection
    ) {
        this.name = name
        val ret = ConfigSupport.compute(this, config, true)
    }

    internal fun createAbilityInstance(file: File): Ability<*> {
        return container.abilityClass.getConstructor().newInstance().apply {
            initConcept(this@AbilityConcept)
            val yaml = YamlConfiguration.loadConfiguration(file)
            val abilityConfig = yaml.createSection("ABILITIES")
            for ((abilityName, value) in abilityConfig.getValues(false)) {
                if (value !is ConfigurationSection) continue
                initialize(abilityName, container, value)
            }
        }
    }

    open fun onInitialize() {}
}