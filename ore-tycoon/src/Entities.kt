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
import io.github.ayfri.kore.helpers.displays.entities.DisplayEntity
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

private fun tagList(tags: Array<out String>) = nbtListOf(listOf(ENTITY) + tags)

/** Full brightness, so holograms and machines stay readable at night. The helpers' `Brightness` names the sky field `light`. */
private fun NbtCompoundBuilder.fullBright() {
	this["brightness"] = nbt {
		this["block"] = 15
		this["sky"] = 15
	}
}

/** The helpers' `billboardMode` is written as `billboard_mode`, a key the game ignores, so it is set here. */
fun NbtCompoundBuilder.billboard(mode: BillboardMode) {
	this["billboard"] = mode.name.lowercase()
}

/** Summons [display] with the NBT built by Kore's display helpers, plus pack tags and [extra] root NBT. */
fun Function.summonDisplay(display: DisplayEntity, pos: Vec3, vararg tags: String, extra: NbtCompoundBuilder.() -> Unit = {}) =
	summon(display.entityType, pos, NbtCompound(display.toNbt() + nbt {
		this["Tags"] = tagList(tags)
		fullBright()
		extra()
	}))

/** A block or item floating at [pos], spinning when tagged `ot.spin`: `teleport_duration` smooths each quarter turn. */
fun Function.summonItem(item: ItemArgument, pos: Vec3, scale: Float, vararg tags: String, extra: NbtCompoundBuilder.() -> Unit = {}) =
	summonDisplay(itemDisplay {
		item(item)
		transformation { this.scale = Vec3f(scale, scale, scale) }
	}, pos, *tags) {
		this["teleport_duration"] = 20
		extra()
	}

/** A hologram always facing the player horizontally, its [text] resolved by the game when summoned. */
fun Function.summonText(text: ChatComponents, pos: Vec3, scale: Float, vararg tags: String, extra: NbtCompoundBuilder.() -> Unit = {}) =
	summonDisplay(textDisplay {
		transformation { this.scale = Vec3f(scale, scale, scale) }
	}, pos, *tags) {
		billboard(BillboardMode.VERTICAL)
		this["text"] = text.toNbtTag()
		this["line_width"] = 400
		extra()
	}

/** An invisible clickable box, its bottom center at [pos]: the game records who hit or used it. */
fun Function.summonInteraction(pos: Vec3, width: Float, height: Float, vararg tags: String) =
	summon(EntityTypes.INTERACTION, pos, nbt {
		this["Tags"] = tagList(arrayOf("ot.click", *tags))
		this["width"] = width
		this["height"] = height
		this["response"] = true
	})
