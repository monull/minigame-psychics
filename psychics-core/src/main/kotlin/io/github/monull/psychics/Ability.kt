package io.github.monull.psychics

import io.github.monull.psychics.util.Times
import io.github.monun.tap.ref.getValue
import io.github.monun.tap.ref.weaky
import org.bukkit.configuration.ConfigurationSection
import kotlin.math.max

abstract class Ability<T: AbilityConcept> {

    lateinit var concept: T
        private set

    var cooldownTime: Long = 0L
        get() {
            return max(0L, field - Times.current)
        }

    internal fun updateCooldown(ticks: Int = (cooldownTime / 50L).toInt()) {

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

    fun checkState() {
        psychic.checkState()
    }

    fun checkEnabled() {
        psychic.checkEnabled()
    }
}