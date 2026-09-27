import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.localPos
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.tp
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.helpers.displays.entities.BillboardMode
import io.github.ayfri.kore.utils.set

/**
 * Each second, every owned drill throws its ore to the core: up and forward, then into the core. Each hop is a plain
 * `tp` that the client smooths over `teleport_duration` ticks, so a flight costs three commands for the whole batch.
 */
class Flyers(dataPack: DataPack) {
	private val core = vec3(center(0), CORE_Y, center(0))

	private fun stage(index: Int) = tagged(EntityTypes.ITEM_DISPLAY, "ot.flyer_$index", single = false)

	private fun Function.advance(from: Int) {
		tag(stage(from)) { add("ot.flyer_${from + 1}") }
		tag(stage(from)) { remove("ot.flyer_$from") }
	}

	val launch = dataPack.function("launch", directory = "flyers") {
		Tier.entries.forEach { tier ->
			execute {
				ifCondition { owned.getValue(tier).value greaterThanOrEqualTo 1 }
				run {
					summonItem(tier.product, vec3(center(tier.x), FLOOR_Y + 2.9, center(tier.z)), 0.45f, "ot.flyer", "ot.flyer_0") {
						this["teleport_duration"] = 9
						billboard(BillboardMode.CENTER)
					}
				}
			}
		}
	}

	val rise = dataPack.function("rise", directory = "flyers") {
		execute {
			asTarget(stage(0))
			at(self())
			facing(core)
			run { tp(self(), vec3(0.localPos, 1.5.localPos, 4.5.localPos)) }
		}
		advance(0)
	}

	val dive = dataPack.function("dive", directory = "flyers") {
		tp(stage(1), core)
		advance(1)
	}

	val land = dataPack.function("land", directory = "flyers") {
		execute {
			ifCondition { entity(stage(2)) }
			run { particle(Particles.ELECTRIC_SPARK, core, vec3(0.8, 0.8, 0.8), 0.1, 10) }
		}
		kill(stage(2))
	}
}
