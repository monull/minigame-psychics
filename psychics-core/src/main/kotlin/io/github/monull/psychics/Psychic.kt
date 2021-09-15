package io.github.monull.psychics

import com.google.common.collect.ImmutableList
import io.github.monull.psychics.plugin.PsychicsPlugin
import io.github.monun.tap.event.RegisteredEntityListener
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.fake.FakeProjectileManager
import io.github.monun.tap.ref.weaky
import io.github.monun.tap.ref.getValue
import io.github.monun.tap.task.Ticker
import org.bukkit.configuration.ConfigurationSection
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class Psychic internal constructor(
    val concept: PsychicConcept
) {
    lateinit var plugin: PsychicsPlugin
        private set

    lateinit var manager: PsychicManager
        private set

    var times = 0L
        private set

    val abilities: List<Ability<*>>

    var isEnabled = false
        set(value) {
            checkState()

            if (field != value) {
                field = value

                if (value) {
                    onEnable()
                } else {
                    onDisable()
                }
            }
        }

    var valid = true
        private set

    lateinit var esper: Esper
        private set

    private lateinit var ticker: Ticker

    private lateinit var projectiles: FakeProjectileManager

    private lateinit var listeners: ArrayList<RegisteredEntityListener>

    private lateinit var fakeEntities: MutableSet<FakeEntity>


    init {
        abilities = ImmutableList.copyOf(concept.abilityConcepts.map { concept ->
            concept.createAbilityInstance()
        })

    }

    internal fun initialize(plugin: PsychicsPlugin, manager: PsychicManager) {
        this.plugin = plugin
        this.manager = manager

        for (ability in abilities) {
            ability.initPsychic(this)
            ability.runCatching { onInitialize() }.onFailure { it.printStackTrace() }
        }
    }

    internal fun attach(esper: Esper) {
        require(!this::esper.isInitialized) { "Cannot redefine epser" }

        val player = esper.player

        val delegate by weaky(esper)
        this.esper = delegate
        ticker = Ticker.precision()
        projectiles = FakeProjectileManager()
        listeners = arrayListOf()
        fakeEntities = Collections.newSetFromMap(WeakHashMap<FakeEntity, Boolean>())

        for (ability in abilities) {
            ability.runCatching { onAttach() }.onFailure { it.printStackTrace() }
        }
    }

    private fun detach() {

        for (ability in abilities) {
            ability.runCatching {
                cooldownTime = 0L
                onDetach()
            }.onFailure(Throwable::printStackTrace)
        }
    }

    private fun onEnable() {

    }

    private fun onDisable() {

    }
    /**
     * 능력 유효 여부를 체크합니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     */
    fun checkState() {
        require(valid) { "Invalid Psychic@${System.identityHashCode(this).toString(16)}" }
    }

    /**
     * 능력 활성화 여부를 체크합니다.
     *
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun checkEnabled() {
        require(isEnabled) { "Disabled Psychic@${System.identityHashCode(this).toString(16)}" }
    }

    companion object {
        internal const val NAME = "name"
        private const val MANA = "mana"
        private const val TIMES = "time"
        private const val ENABLED = "enabled"
        private const val ABILITIES = "abilities"
    }

    internal fun save(config: ConfigurationSection) {
        config[NAME] = concept.name
        config[TIMES] = times
        config[ENABLED] = isEnabled

        val abilitiesSection = config.createSection(ABILITIES)

        for (ability in abilities) {
            val abilityName = ability.concept.name
            val abilitySection = abilitiesSection.createSection(abilityName)

            ability.save(abilitySection)
        }
    }

    internal fun load(config: ConfigurationSection) {
        times = max(0L, config.getLong(TIMES))

        config.getConfigurationSection(ABILITIES)?.let { abilitiesSection ->
            for (ability in abilities) {
                val abilityName = ability.concept.name
                val abilitySection = abilitiesSection.getConfigurationSection(abilityName)

                if (abilitySection != null)
                    ability.load(abilitySection)
            }
        }

        isEnabled = config.getBoolean(ENABLED)
    }
}