package io.github.monull.psychics.util

object Times {
    private val INIT_NANO_TIME = System.nanoTime()

    val current: Long
        get() {
            return (System.nanoTime() - INIT_NANO_TIME) / (1000L * 1000L)
        }
}