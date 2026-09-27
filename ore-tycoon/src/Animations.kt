import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set

private fun floats(vararg values: Float) = nbtListOf(*values)

fun itemNbt(item: ItemArgument) = nbt {
	this["id"] = item.asId()
	this["count"] = 1
}

/** A decomposed display transformation, uniformly scaled and lifted by [lift] blocks. */
fun transformationNbt(scale: Float, lift: Float = 0f) = nbt {
	this["left_rotation"] = floats(0f, 0f, 0f, 1f)
	this["right_rotation"] = floats(0f, 0f, 0f, 1f)
	this["scale"] = floats(scale, scale, scale)
	this["translation"] = floats(0f, lift, 0f)
}

/** Smoothly rescales a display entity over [ticks] ticks, the client interpolates from its current state. */
fun Function.animate(target: DataArgument, scale: Float, ticks: Int, lift: Float = 0f, item: ItemArgument? = null) =
	data(target).merge(nbt {
		item?.let { this["item"] = itemNbt(it) }
		this["interpolation_duration"] = ticks
		this["start_interpolation"] = 0
		this["transformation"] = transformationNbt(scale, lift)
	})
