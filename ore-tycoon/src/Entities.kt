import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.Vec3f
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.helpers.displays.entities.BillboardMode
import io.github.ayfri.kore.helpers.displays.entities.Brightness
import io.github.ayfri.kore.helpers.displays.entities.DisplayEntity
import io.github.ayfri.kore.helpers.displays.entities.ItemDisplay
import io.github.ayfri.kore.helpers.displays.entities.TextDisplay
import io.github.ayfri.kore.helpers.displays.item
import io.github.ayfri.kore.helpers.displays.itemDisplay
import io.github.ayfri.kore.helpers.displays.maths.transformation
import io.github.ayfri.kore.helpers.displays.textDisplay
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

/** Every entity of the factory is found by type and scoreboard tag, `@e` without coordinates searches all dimensions. */
fun tagged(type: EntityTypeArgument, tag: String, single: Boolean = true) = allEntities(single) {
	this.type = type
	this.tag = tag
}

object Entities {
	val core = tagged(EntityTypes.ITEM_DISPLAY, "ot.core")
	val coreLabel = tagged(EntityTypes.TEXT_DISPLAY, "ot.core_label")
	val forgeItem = tagged(EntityTypes.ITEM_DISPLAY, "ot.forge_item")
	val forgeLabel = tagged(EntityTypes.TEXT_DISPLAY, "ot.forge_label")
	val altarLabel = tagged(EntityTypes.TEXT_DISPLAY, "ot.altar_label")
	val golden = tagged(EntityTypes.ITEM_DISPLAY, "ot.golden", single = false)
	val clickable = tagged(EntityTypes.INTERACTION, "ot.click", single = false)
	val all = allEntities { tag = ENTITY }

	fun machine(tier: Tier) = tagged(EntityTypes.ITEM_DISPLAY, "ot.machine.${tier.id}")
	fun label(tier: Tier) = tagged(EntityTypes.TEXT_DISPLAY, "ot.label.${tier.id}")
}

/** Holograms and machines stay readable at night. */
private val FULL_BRIGHT = Brightness(block = 15, sky = 15)

/** Summons [display] at full brightness with the pack tags, plus [extra] root NBT the display helpers don't cover. */
private fun Function.summonDisplay(display: DisplayEntity, pos: Vec3, tags: Array<out String>, extra: NbtCompoundBuilder.() -> Unit) =
	summon(display.entityType, pos, NbtCompound(display.apply { brightness = FULL_BRIGHT }.toNbt() + nbt {
		this["Tags"] = nbtListOf(ENTITY, *tags)
		extra()
	}))

/**
 * A block or item floating at [pos], spinning when tagged `ot.spin`: [teleportDuration] smooths each `tp` on the client.
 * [glowColor] (`0xRRGGBB`) outlines it, written as raw NBT since the helpers encode `glow_color_override` as a string.
 */
fun Function.summonItem(
	item: ItemArgument,
	pos: Vec3,
	scale: Float,
	vararg tags: String,
	teleportDuration: Int = 20,
	glowColor: Int? = null,
	configure: ItemDisplay.() -> Unit = {},
) = summonDisplay(itemDisplay {
	item(item)
	transformation { this.scale = Vec3f(scale, scale, scale) }
	configure()
}, pos, tags) {
	this["teleport_duration"] = teleportDuration
	glowColor?.let {
		this["Glowing"] = true
		this["glow_color_override"] = it
	}
}

/**
 * A hologram facing the player horizontally, its [text] resolved by the game when summoned. Without a [background], the
 * text keeps the default dark plate, written as raw NBT since the helpers encode it as a string.
 */
fun Function.summonText(
	text: ChatComponents,
	pos: Vec3,
	scale: Float,
	vararg tags: String,
	background: Int? = null,
	configure: TextDisplay.() -> Unit = {},
) = summonDisplay(textDisplay {
	billboardMode = BillboardMode.VERTICAL
	lineWidth = 400
	transformation { this.scale = Vec3f(scale, scale, scale) }
	configure()
}, pos, tags) {
	this["text"] = text.toNbtTag()
	background?.let { this["background"] = it }
}

/** An invisible clickable box, its bottom center at [pos]: the game records who hit or used it. */
fun Function.summonInteraction(pos: Vec3, width: Float, height: Float, vararg tags: String) =
	summon(EntityTypes.INTERACTION, pos, nbt {
		this["Tags"] = nbtListOf(ENTITY, "ot.click", *tags)
		this["width"] = width
		this["height"] = height
		this["response"] = true
	})
