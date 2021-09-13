package io.github.monull.psychics

import io.github.monull.psychics.plugin.PsychicPlugin
import io.github.monun.tap.fake.FakeEntity
import org.bukkit.Location
import org.bukkit.entity.Entity


class Psychic(val abilities: List<Ability<*>>) {
    lateinit var plugin: PsychicPlugin
        private set

    lateinit var manager: PsychicManager
        private set

    lateinit var esper: Esper
        private set

    /**
     * 능력 유효상태입니다.
     */
    var valid = true
        private set

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

    internal fun initialize(plugin: PsychicPlugin, manager: PsychicManager) {
        this.plugin = plugin
        this.manager = manager

        for (ability in abilities) {
            ability.initPsychic(this)
            ability.runCatching { onInitialize() }.onFailure { it.printStackTrace() }
        }
        isEnabled = true
    }

    internal fun attach(esper: Esper) {
        this.esper = esper
    }

    private fun onEnable() {
        isEnabled = true

        for (ability in abilities) {
            ability.runCatching { onEnable() }.onFailure(Throwable::printStackTrace)
        }
    }

    private fun onDisable() {
        for (ability in abilities) {
            ability.runCatching { onDisable() }.onFailure(Throwable::printStackTrace)
        }
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

    fun spawnFakeEntity(location: Location, entityClass: Class<out Entity>): FakeEntity {
        checkState()
        checkEnabled()

        val fakeEntity = manager.plugin.fakeEntityServer.spawnEntity(location, entityClass)

        return fakeEntity
    }
}