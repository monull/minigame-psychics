package io.github.monull.psychics

import com.google.common.collect.ImmutableList
import io.github.monull.psychics.plugin.PsychicsPlugin
import io.github.monun.tap.event.RegisteredEntityListener
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.fake.FakeProjectileManager
import io.github.monun.tap.ref.weaky
import io.github.monun.tap.ref.getValue
import io.github.monun.tap.task.Ticker
import io.github.monun.tap.task.TickerTask
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
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
        isEnabled = true
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
        for (ability in abilities) {
            ability.runCatching { onEnable() }.onFailure(Throwable::printStackTrace)
        }
    }

    private fun onDisable() {
        ticker.cancelAll()
        projectiles.clear()
        listeners.run {
            for (registeredEntityListener in this) {
                registeredEntityListener.unregister()
            }
            clear()
        }
        fakeEntities.run {
            for (fakeEntity in this) {
                fakeEntity.remove()
            }
            clear()
        }
        for (ability in abilities) {
            ability.runCatching { onDisable() }.onFailure(Throwable::printStackTrace)
        }
    }

    /**
     * 태스크를 delay만큼 후에 실행합니다.
     *
     * 능력이 비활성화 될 때 취소됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun runTask(runnable: Runnable, delay: Long): TickerTask {
        checkState()
        checkEnabled()

        return ticker.runTask(runnable, delay)
    }

    /**
     * 태스크를 delay만큼 후 period마다 주기적으로 실행합니다.
     *
     * 능력이 비활성화 될 때 취소됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): TickerTask {
        checkState()
        checkEnabled()

        return ticker.runTaskTimer(runnable, delay, period)
    }

    /**
     * 이벤트를 등록합니다.
     *
     * 범위는 해당 [Psychic]이 부여된 [org.bukkit.entity.Player]객체로 국한됩니다.
     *
     * 능력이 비활성화 될 때 해제됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun registerEvents(listener: Listener) {
        checkState()
        checkEnabled()

        listeners.add(plugin.entityEventManager.registerEvents(esper.player, listener))
    }

    /**
     * 발사체를 발사합니다.
     *
     * 능력이 비활성화 될 때 제거됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun launchProjectile(location: Location, projectile: PsychicProjectile) {
        checkState()
        checkEnabled()

        projectile.psychic = this
        projectiles.launch(location, projectile)
    }

    /**
     * 가상 [Entity]를 생성합니다.
     *
     * 능력이 비활성화 될 때 제거됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun spawnFakeEntity(location: Location, entityClass: Class<out Entity>): FakeEntity {
        checkState()
        checkEnabled()

        val fakeEntity = manager.plugin.fakeEntityServer.spawnEntity(location, entityClass)
        fakeEntities.add(fakeEntity)

        return fakeEntity
    }

    /**
     * 가상 [org.bukkit.entity.FallingBlock]을 생성합니다.
     *
     * 능력이 비활성화 될 때 제거됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun spawnFakeFallingBlock(location: Location, blockData: BlockData): FakeEntity {
        checkState()
        checkEnabled()

        val fakeEntity = manager.plugin.fakeEntityServer.spawnFallingBlock(location, blockData)
        fakeEntities.add(fakeEntity)

        return fakeEntity
    }

    /**
     * 가상 [org.bukkit.entity.Item]을 생성합니다.
     *
     * 능력이 비활성화 될 때 제거됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun spawnItem(location: Location, itemStack: ItemStack): FakeEntity {
        checkState()
        checkEnabled()

        val fakeEntity = manager.plugin.fakeEntityServer.spawnItem(location, itemStack)
        fakeEntities.add(fakeEntity)

        return fakeEntity
    }

    /**
     * Marker로 설정된 [org.bukkit.entity.ArmorStand]를 생성합니다.
     *
     * 능력이 비활성화 될 때 제거됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun spawnMarker(location: Location): FakeEntity {
        return spawnFakeEntity(location, ArmorStand::class.java).apply {
            updateMetadata<ArmorStand> {
                isMarker = true
                isInvisible = true
            }
        }
    }

    /**
     * Marker로 설정된 [org.bukkit.entity.ArmorStand]를 생성 후 인수로 받은 [FakeEntity]를 승객으로 설정합니다.
     *
     * 능력이 비활성화 될 때 제거됩니다.
     *
     * @exception IllegalArgumentException 유효하지 않은 객체일때 발생
     * @exception IllegalArgumentException 활성화되지 않은 객체일때 발생
     */
    fun marker(passenger: FakeEntity): FakeEntity {
        return spawnMarker(passenger.location).apply { addPassenger(passenger) }
    }

    fun broadcast(string: String) {
        Bukkit.broadcast(Component.text(string))
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