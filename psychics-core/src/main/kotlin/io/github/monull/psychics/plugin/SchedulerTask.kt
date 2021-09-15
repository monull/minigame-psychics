package io.github.monull.psychics.plugin

import io.github.monull.psychics.PsychicManager
import io.github.monun.tap.fake.FakeEntityServer

class SchedulerTask(
    private val psychicManager: PsychicManager,
    private val fakeEntityServer: FakeEntityServer
): Runnable {
    override fun run() {
        for (esper in psychicManager.espers) {
            esper.psychic?.run {
                if (isEnabled) {

                }
            }
        }
        fakeEntityServer.update()
    }
}