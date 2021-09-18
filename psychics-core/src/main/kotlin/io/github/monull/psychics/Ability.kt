package io.github.monull.psychics

import io.github.monull.psychics.attribute.EsperStatistic
import io.github.monull.psychics.damage.Damage
import io.github.monull.psychics.damage.psychicDamage
import io.github.monull.psychics.damage.psychicHeal
import io.github.monull.psychics.format.decimalFormat
import io.github.monull.psychics.util.Times
import io.github.monun.tap.ref.getValue
import io.github.monun.tap.ref.weaky
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerEvent
import kotlin.math.max

abstract class Ability<T: AbilityConcept> {

    lateinit var concept: T
        private set

    var cooldownTime: Long = 0L
        get() {
            return max(0L, field - Times.current)
        }
    set(value) {
        checkState()

        val times = max(0L, value)
        field = Times.current + times
        updateCooldown((value / 50L).toInt())
    }

    internal fun updateCooldown(ticks: Int = (cooldownTime / 50L).toInt()) {
        val wand = concept.wand
        if (wand != null) esper.player.setCooldown(wand.type, ticks)
    }

    var durationTime: Long = 0L
        get() {
            return max(0L, field - Times.current)
        }
        set(value) {
            val times = max(0L, value)
            field = Times.current + times
        }

    lateinit var psychic: Psychic

    val esper
        get() = psychic.esper

    @Suppress("UNCHECKED_CAST")
    internal fun initConcept(concept: AbilityConcept) {
        this.concept = concept as T
    }

    internal fun initPsychic(psychic: Psychic) {
        val delegate by weaky(psychic)
        this.psychic = delegate
    }

    internal fun save(config: ConfigurationSection) {
        config[COOLDOWN_TIME] = cooldownTime

        runCatching {
            onSave(config)
        }.onFailure {
            it.printStackTrace()
        }
    }

    internal fun load(config: ConfigurationSection) {
        cooldownTime = max(0L, config.getLong(COOLDOWN_TIME))

        runCatching {
            onLoad(config)
        }.onFailure {
            it.printStackTrace()
        }
    }

    companion object {
        private const val COOLDOWN_TIME = "cooldown-time"
    }

    /**
     * 초기화 후 호출됩니다.
     */
    open fun onInitialize() {}

    /**
     * 플레이어에게 적용 후 호출됩니다.
     */
    open fun onAttach() {}

    /**
     * 플레이어로부터 해제 후 호출됩니다.
     */
    open fun onDetach() {}

    /**
     * 정보를 디스크에 저장 할 때 호출됩니다.
     */
    open fun onSave(config: ConfigurationSection) {}

    /**
     * 정보를 디스크로부터 불러 올 때 호출됩니다.
     */
    open fun onLoad(config: ConfigurationSection) {}


    /**
     * 능력이 활성화 될 때 호출됩니다.
     */
    open fun onEnable() {}

    /**
     * 능력이 비활성화 될 때 호출됩니다.
     */
    open fun onDisable() {}

    /**
     * [LivingEntity]에게 피해를 입힙니다.
     *
     * 기본 인수로 [AbilityConcept]에 정의된 변수를 사용합니다.
     *
     * @exception IllegalArgumentException [AbilityConcept.damage] 인수가 정의되어 있지 않을 때 발생
     */
    fun LivingEntity.psychicDamage(
        damage: Damage = requireNotNull(concept.damage) { "Damage is not defined" },
        knockbackLocation: Location? = esper.player.location,
        knockback: Double = concept.knockback
    ) {
        val type = damage.type
        val amount = esper.getStatistic(damage.stats)

        psychicDamage(this@Ability, type, amount, esper.player, knockbackLocation, knockback)
    }

    /**
     * [LivingEntity]를 치유합니다.
     *
     * 기본 인수로 [AbilityConcept]에 정의된 변수를 사용합니다.
     *
     * @exception IllegalArgumentException [AbilityConcept.healing] 인수가 정의되어 있지 않을 때 발생
     */
    fun LivingEntity.psychicHeal(
        heal: EsperStatistic = requireNotNull(concept.healing) { "Healing is not defined" },
    ) {
        val amount = esper.getStatistic(heal)

        psychicHeal(this@Ability, amount, esper.player)
    }

    /**
     * [LivingEntity]를 치유합니다.
     */
    fun LivingEntity.psychicHeal(
        amount: Double
    ) {
        psychicHeal(this@Ability, amount, esper.player)
    }


    fun checkState() {
        psychic.checkState()
    }

    fun checkEnabled() {
        psychic.checkEnabled()
    }
}

abstract class ActiveAbility<T : AbilityConcept> : Ability<T>() {
    var targeter: (() -> Any?)? = null

    open fun tryCast(
        event: PlayerEvent,
        action: WandAction,
        castingTime: Long = concept.castingTime,
        targeter: (() -> Any?)? = this.targeter
    ) {
        var target: Any? = null

        if (targeter != null) {
            target = targeter.invoke()
        }
        cast(event, action, castingTime, target)
    }

    protected fun cast(
        event: PlayerEvent,
        action: WandAction,
        castingTime: Long,
        target: Any? = null
    ) {
        checkState()

        if (castingTime > 0) {

        } else {
            onCast(event, action, target)
        }
    }

    abstract fun onCast(event: PlayerEvent, action: WandAction, target: Any?)

    open fun onChannel(channel: Channel) {}

    open fun onInterrupt(channel: Channel) {}

    enum class WandAction {
        LEFT_CLICK,
        RIGHT_CLICK
    }
}