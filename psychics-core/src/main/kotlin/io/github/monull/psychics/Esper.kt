package io.github.monull.psychics

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

class Esper(val manager: PsychicManager, player: Player) {
    val player: Player
        get() = requireNotNull(playerRef.get()) { "Cannot get reference as it has already been Garbage"}

    private val playerRef = WeakReference(player)

    private val attributeUniqueId: UUID

    var psychic: Psychic? = null
        private set

    val isOnline
        get() = playerRef.get() != null

    private val dataFile
        get() = File(manager.esperFolder, "${player.uniqueId}.yml")

    init {
        val uniqueId = player.uniqueId

        attributeUniqueId = UUID(uniqueId.leastSignificantBits.inv(), uniqueId.mostSignificantBits.inv())
    }

    private fun setPsychic(concept: PsychicConcept): Psychic {
        val psychic = concept.createInstance()
        this.psychic = psychic
        psychic.attach(this@Esper)
        return psychic
    }

    fun attachPsychic(concept: PsychicConcept): Psychic {

        return setPsychic(concept)
    }

    internal fun removePsychic() {
        psychic?.let { psychic ->
            this.psychic = null
        }
    }

    fun detachPsychic() {
        removePsychic()
    }

    companion object {
        private const val PSYCHIC = "psychic"
    }

    internal fun load() {
        val file = dataFile

        if (!file.exists()) return

        val config = YamlConfiguration.loadConfiguration(file)

        config.getConfigurationSection(PSYCHIC)?.let { psychicConfig ->
            val psychicName = psychicConfig.getString(Psychic.NAME)

            if (psychicName != null) {
                val psychicConcept = manager.getPsychicConcept(psychicName)

                if (psychicConcept == null) {
                    manager.plugin.logger.warning("Failed to attach psychic $psychicName for ${player.name}")
                    return
                }

                val psychic = setPsychic(psychicConcept)
                psychic.load(psychicConfig)
            }
        }
    }

    fun save() {
        val config = YamlConfiguration()

        psychic?.save(config.createSection(PSYCHIC))

        config.save(dataFile)
    }

    internal fun clear() {
        psychic?.let { psychic ->
            this.psychic = null
        }
        playerRef.clear()
    }
}