package io.github.monull.psychics.command

import io.github.monull.psychics.PsychicManager
import io.github.monull.psychics.esper
import io.github.monull.psychics.invfx.InvPsychic
import io.github.monull.psychics.plugin.PsychicsPlugin
import io.github.monun.invfx.openFrame
import io.github.monun.kommand.PluginKommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

object KommandPsychic {
    private lateinit var plugin: PsychicsPlugin
    private lateinit var manager: PsychicManager

    fun register(plugin: PsychicsPlugin, manager: PsychicManager, kommand: PluginKommand) {
        this.plugin = plugin
        this.manager = manager

        kommand.register("psychics", "psy") {
            permission("psychics.commands")

            then("reload") {
                executes {
                    plugin.reloadPsychics()
                    broadcast(text().content("Psychics reload complete.").color(NamedTextColor.GREEN))
                }
            }

            then("abilities") {
                requires { isPlayer }
                executes {
                    val player = player
                    val esper = player.esper
                    val psychic = esper.psychic

                    if (psychic == null) {
                        feedback(text("능력이 없습니다."))
                    } else {
                        player.openFrame(InvPsychic.create(psychic.concept, esper::getStatistic))
                    }
                }
            }
        }
    }
}