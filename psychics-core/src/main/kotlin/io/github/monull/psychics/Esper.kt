package io.github.monull.psychics

import org.bukkit.entity.Player
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

class Esper(
    val manager: PsychicManager,
    player: Player
) {
    val player: Player
        get() = requireNotNull(playerRef.get()) { "Cannot get reference as it has already been Garbage Collected" }

    private val playerRef = WeakReference(player)

    private val attributeUniqueId: UUID

    var psychic: Psychic? = null
        private set

    val isOnline
        get() = playerRef.get() != null

    init {
        val uniqueId = player.uniqueId

        attributeUniqueId = UUID(uniqueId.leastSignificantBits.inv(), uniqueId.mostSignificantBits.inv())

        val list = arrayListOf<Ability<*>>()
        manager.abilityContainersById.forEach { (_, abilityContainer) ->
            val concept = abilityContainer.conceptClass.getConstructor().newInstance()
            concept.container = abilityContainer
            val ability = concept.createAbilityInstance(File(manager.plugin.dataFolder, "config.yml"))
            list += ability
        }
        psychic = Psychic(list)
        psychic!!.attach(this)
        psychic!!.initialize(manager.plugin, manager)
    }

    internal fun clear() {
        psychic?.let {
            this.psychic = null
        }
        playerRef.clear()
    }
}