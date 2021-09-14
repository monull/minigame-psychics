package io.github.monull.psychics

import io.github.monun.tap.fake.FakeProjectile

class PsychicProjectile(maxTicks: Int, range: Double) : FakeProjectile(maxTicks, range) {
    lateinit var psychic: Psychic
        internal set
}