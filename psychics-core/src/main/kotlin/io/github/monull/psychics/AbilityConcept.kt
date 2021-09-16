package io.github.monull.psychics

import io.github.monull.psychics.attribute.EsperStatistic
import io.github.monull.psychics.damage.Damage
import io.github.monun.tap.config.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.boss.BarColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import java.util.logging.Logger

@Name("common")
open class AbilityConcept {

    lateinit var name: String
        private set

    lateinit var container: AbilityContainer
        private set

    lateinit var psychicConcept: PsychicConcept
        private set

    lateinit var logger: Logger
        private set

    /**
     * 표시 이름 (I18N)
     */
    @Config(required = false)
    lateinit var displayName: String
        protected set

    /**
     * 재사용 대기시간
     */
    @Config(required = false)
    @RangeInt(min = 0)
    var cooldownTime = 0L
        protected set

    /**
     * 시전 시간
     */
    @Config(required = false)
    @RangeInt(min = 0)
    var castingTime = 0L
        protected set

    /**
     * 시전 시간 -> 집중 시간
     * 스킬을 시전 시 외부에서 중단 가능
     */
    @Config(required = false)
    var interruptible = false
        protected set

    /**
     * 시전 상태 바 색상
     */
    @Config(required = false)
    var castingBarColor: BarColor? = null

    /**
     * 지속 시간
     */
    @Config(required = false)
    var durationTime = 0L
        protected set

    /**
     * 사거리
     */
    @Config(required = false)
    @RangeDouble(min = 0.0)
    var range = 0.0
        protected set

    /**
     * 피해량
     */
    @Config(required = false)
    var damage: Damage? = null
        protected set

    /**
     * 넉백
     */
    @Config(required = false)
    var knockback = 0.0

    /**
     * 치유량
     */
    @Config(required = false)
    var healing: EsperStatistic? = null
        protected set

    @Config("wand", required = false)
    private var _wand: ItemStack? = null

    internal val internalWand
        get() = _wand

    /**
     * 능력과 상호작용하는 [ItemStack]
     */
    var wand
        get() = _wand?.clone()
        protected set(value) {
            _wand = value?.clone()
        }

    @Config("description")
    private var descriptionRaw: List<String> = ArrayList(0)

    var description: List<Component> = emptyList()

    internal fun initialize(
        name: String,
        container: AbilityContainer,
        psychicConcept: PsychicConcept,
        config: ConfigurationSection
    ): Boolean {
        this.name = name
        this.container = container
        this.psychicConcept = psychicConcept
        if (!this::displayName.isInitialized)
            this.displayName = container.description.name

        val gson = GsonComponentSerializer.gson()
        descriptionRaw = description.map { gson.serialize(it) }

        val ret = ConfigSupport.compute(this, config, true)
        this.description = descriptionRaw.map { gson.deserialize(it) }

        return ret
    }

    internal fun createAbilityInstance(): Ability<*> {
        return container.abilityClass.getConstructor().newInstance().apply {
            initConcept(this@AbilityConcept)
        }
    }

    /**
     * 필드 변수 적용 후 호출
     */
    open fun onInitialize() {}
}