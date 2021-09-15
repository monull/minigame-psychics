package io.github.monull.psychics

import io.github.monun.tap.config.Config
import io.github.monun.tap.config.ConfigSupport
import io.github.monun.tap.config.Name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.configuration.ConfigurationSection
import java.util.logging.Logger

@Name("common")
open class AbilityConcept {

    lateinit var name: String
        private set

    lateinit var container: AbilityContainer
        private set

    lateinit var psychicConcept: PsychicConcept
        private set

    lateinit var logger: Logger
        private set

    /**
     * 표시 이름 (I18N)
     */
    @Config(required = false)
    lateinit var displayName: String
        protected set

    @Config("description")
    private var descriptionRaw: List<String> = ArrayList(0)

    var description: List<Component> = emptyList()

    internal fun initialize(
        name: String,
        container: AbilityContainer,
        psychicConcept: PsychicConcept,
        config: ConfigurationSection
    ): Boolean {
        this.name = name
        this.container = container
        this.psychicConcept = psychicConcept
        if (!this::displayName.isInitialized)
            this.displayName = container.description.name

        val gson = GsonComponentSerializer.gson()
        descriptionRaw = description.map { gson.serialize(it) }

        val ret = ConfigSupport.compute(this, config, true)
        this.description = descriptionRaw.map { gson.deserialize(it) }

        return ret
    }

    internal fun createAbilityInstance(): Ability<*> {
        return container.abilityClass.getConstructor().newInstance().apply {
            initConcept(this@AbilityConcept)
        }
    }

    /**
     * 필드 변수 적용 후 호출
     */
    open fun onInitialize() {}
}