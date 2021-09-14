package io.github.monull.psychics

import com.google.common.collect.ImmutableSortedMap
import io.github.monull.psychics.loader.AbilityLoader
import io.github.monull.psychics.plugin.PsychicPlugin
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.jar.JarFile
import java.util.logging.Logger
import kotlin.math.min

class PsychicManager(
    val plugin: PsychicPlugin,
    val logger: Logger,
    val abilitiesFolder: File
) {
    private val abilityLoader = AbilityLoader()

    lateinit var abilityContainersById: Map<String, AbilityContainer>
        private set

    lateinit var psychic: Psychic
        private set

    private val espersByPlayer = IdentityHashMap<Player, Esper>(Bukkit.getMaxPlayers())

    internal fun reload() {
        abilityLoader.clear()


    }

    internal fun addPlayer(player: Player) {
        espersByPlayer.computeIfAbsent(player) {
            val esper = Esper(this, it)
            esper
        }
    }

    internal fun removePlayer(player: Player) {
        espersByPlayer.remove(player)?.let { esper ->
            esper.clear()
        }
    }

    fun getEsper(player: Player): Esper? {
        return espersByPlayer[player]
    }

    private fun getAbilityFiles(): Array<File> {
        abilitiesFolder.mkdirs()
        return abilitiesFolder.listFiles { file -> !file.isDirectory && file.name.endsWith(".jar") }
            ?: return emptyArray()
    }

    internal fun updateAbilities() {
        val abilityFiles = getAbilityFiles()
        if (abilityFiles.isEmpty()) return

        val updateFolder = File(abilitiesFolder, "update")
        val updated = arrayListOf<File>()

        for (abilityFile in abilityFiles) {
            val updateFile = File(updateFolder, abilityFile.name)

            if (updateFile.exists()) {
                updateFile.runCatching {
                    copyTo(abilityFile, true)
                }.onSuccess {
                    updated += it
                    updateFile.runCatching { delete() }
                }.onFailure {
                    it.printStackTrace()
                    logger.warning("Failed to update ability ${updateFile.nameWithoutExtension}")
                }
            }
        }

        logger.info("Updated abilities(${updated.count()}): ")

        updated.forEach { file ->
            logger.info("  - ${file.nameWithoutExtension}")
        }
    }

    internal fun loadAbilities() {
        val descriptions = loadAbilityDescriptions()
        val map = TreeMap<String, AbilityContainer>()

        for ((file, description) in descriptions) {
            abilityLoader.runCatching {
                map[description.artifactId] = load(file, description)
            }.onFailure { exception: Throwable ->
                exception.printStackTrace()
                logger.warning("Failed to load Ability ${file.name}")
            }
        }

        logger.info("Loaded abilities(${map.count()}):")

        for ((id, container) in map) {
            logger.info("  - $id v${container.description.version}")
        }

        abilityContainersById = ImmutableSortedMap.copyOf(map)
    }

    private fun loadAbilityDescriptions(): List<Pair<File, AbilityDescription>> {
        val abilityFiles = getAbilityFiles()

        val byId = TreeMap<String, Pair<File, AbilityDescription>>()

        for (abilityFile in abilityFiles) {
            abilityFile.runCatching { getAbilityDescription() }
                .onSuccess { description ->
                    val id = description.artifactId
                    val other = byId[id]

                    if (other != null) {
                        val otherDescription = other.second
                        var legacy: File = abilityFile

                        if (description.version.compareVersion(otherDescription.version) > 0) { //높은 버전일경우
                            byId[id] = Pair(abilityFile, description)
                            legacy = other.first
                        }

                        logger.warning("Ambiguous Ability file name. ${legacy.name}")
                    } else {
                        byId[id] = Pair(abilityFile, description)
                    }
                }
                .onFailure { exception ->
                    exception.printStackTrace()

                    logger.warning("Failed to load AbilityDescription ${abilityFile.name}")
                }
        }

        return byId.values.toList()
    }

    private fun findAbilityContainer(name: String): List<AbilityContainer> {
        if (name.startsWith(".")) {
            val list = arrayListOf<AbilityContainer>()

            for ((key, container) in abilityContainersById) {
                if (key.endsWith(name))
                    list += container
            }

            return list
        }

        val container = abilityContainersById[name]

        return if (container != null) listOf(container) else emptyList()
    }

    companion object {
        private const val ABILITIES = "abilities"
        private const val ABILITY = "ability"
    }
}

private fun File.getAbilityDescription(): AbilityDescription {
    JarFile(this).use { jar ->
        jar.getJarEntry("ability.yml")?.let { entry ->
            jar.getInputStream(entry).bufferedReader(StandardCharsets.UTF_8).use { reader ->
                val config = YamlConfiguration.loadConfiguration(reader)

                return AbilityDescription(config)
            }
        }
    }

    error("Failed to open JarFile $name")
}

private fun String.compareVersion(other: String): Int {
    val splitA = this.split('.')
    val splitB = other.split('.')
    val count = min(splitA.count(), splitB.count())

    for (i in 0 until count) {
        val a = splitA[i]
        val b = splitB[i]

        val numberA = a.toIntOrNull()
        val numberB = b.toIntOrNull()

        if (numberA != null && numberB != null) {
            val result = numberA.compareTo(numberB)

            if (result != 0)
                return result
        } else {
            if (numberA != null) return 1
            if (numberB != null) return -1

            val result = a.compareTo(b)

            if (result != 0)
                return result
        }
    }

    return splitA.count().compareTo(splitB.count())
}