import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.returnIf
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.macro
import io.github.ayfri.kore.scoreboard.*
import net.benwoodworth.knbt.NbtString

private val UNITS = listOf(1_000_000_000 to "B", 1_000_000 to "M", 1_000 to "K")

/** The build-time twin of [NumberFormatter], for numbers known in Kotlin such as base productions. */
fun Int.short(): String {
	val (unit, suffix) = UNITS.firstOrNull { this >= it.first } ?: return toString()
	val whole = this / unit
	val rest = this % unit
	return when {
		whole >= 100 -> "$whole$suffix"
		whole >= 10 -> "$whole.${rest / (unit / 10)}$suffix"
		else -> "$whole.${(rest / (unit / 100)).toString().padStart(2, '0')}$suffix"
	}
}

/** Integer operands for `scoreboard players operation`, which has no literal form, all set once by [setAll] on load. */
object Constants {
	private val values = sortedSetOf<Int>()

	operator fun get(value: Int) = global("$value").also { values += value }

	context(fn: Function)
	fun setAll() = values.forEach { global("$it").set(it) }
}

/**
 * Turns a score into a short string with three significant digits: `950`, `1.05K`, `12.3K`, `456M`.
 *
 * Commands can't build strings, so the digits are computed with scoreboard math, stored as ints, then pasted into a
 * string by a macro: `$data modify storage ore_tycoon:data hud.out set value "$(whole).$(pad)$(fraction)$(suffix)"`.
 */
class NumberFormatter(dataPack: DataPack) {
	private val input = global("format.input")
	private val whole = global("format.whole")
	private val fraction = global("format.fraction")

	private val plain = dataPack.function("plain", directory = "format") {
		data(STORAGE).modify("hud.out", NbtString("${macro("whole")}${macro("suffix")}"))
	}

	private val decimal = dataPack.function("decimal", directory = "format") {
		data(STORAGE).modify("hud.out", NbtString("${macro("whole")}.${macro("pad")}${macro("fraction")}${macro("suffix")}"))
	}

	private val units = UNITS.map { (unit, suffix) ->
		unit to dataPack.function("unit_${suffix.lowercase()}", directory = "format") {
			whole setTo input
			whole /= Constants[unit]
			fraction setTo input
			fraction %= Constants[unit]
			data(STORAGE).modify("args.suffix", NbtString(suffix))
			data(STORAGE).modify("args.pad", NbtString(""))
			whole.copyTo(STORAGE, "args.whole", DataType.INT)
			returnIf({ whole.value greaterThanOrEqualTo 100 }) { function(plain, arguments = STORAGE, path = "args") }

			fraction /= Constants[unit / 100]
			execute {
				ifCondition { whole.value greaterThanOrEqualTo 10 }
				run { fraction /= Constants[10] }
			}
			execute {
				ifCondition {
					whole.value lessThanOrEqualTo 9
					fraction.value lessThanOrEqualTo 9
				}
				run { data(STORAGE).modify("args.pad", NbtString("0")) }
			}
			fraction.copyTo(STORAGE, "args.fraction", DataType.INT)
			function(decimal, arguments = STORAGE, path = "args")
		}
	}

	private val entry = dataPack.function("number", directory = "format") {
		units.forEach { (unit, function) -> returnIf({ input.value greaterThanOrEqualTo unit }) { function(function) } }
		input.copyTo(STORAGE, "args.whole", DataType.INT)
		data(STORAGE).modify("args.suffix", NbtString(""))
		function(plain, arguments = STORAGE, path = "args")
	}

	/** Formats [source] into `hud.<key>` of [STORAGE], ready for an interpreted NBT text component. */
	context(fn: Function)
	fun format(source: ScoreboardEntity, key: String) {
		input setTo source
		fn.function(entry)
		fn.data(STORAGE).modify("hud.$key", STORAGE, "hud.out")
	}
}
