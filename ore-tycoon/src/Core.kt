import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.localPos
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.block
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.particle.particles
import io.github.ayfri.kore.commands.particle.types.block
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.helpers.displays.entities.BillboardMode
import io.github.ayfri.kore.scoreboard.*
import io.github.ayfri.kore.utils.set

/** The giant ore in the middle: every hit mines coins, squashes it and throws a floating `+N` at the player. */
class Core(dataPack: DataPack, economy: Economy, formatter: NumberFormatter, hud: Hud) {
	/** Runs at the hit point: a `+N` text that the tick loop lifts and fades, and chips of the current core block. */
	private val burst = dataPack.function("burst", directory = "core") {
		summonText(textComponent("+", Color.GOLD) + hudText("gain", Color.GOLD, bold = true), vec3(), 0.9f, "ot.pop", "ot.pop_new") {
			billboard(BillboardMode.CENTER)
			this["background"] = 0
			this["shadow"] = true
		}
		Level.entries.forEach { stage ->
			execute {
				ifCondition { level.value equalTo stage.number }
				run { particles { block(block(stage.core.asId().substringAfter(':')), vec3(), vec3(0.2, 0.2, 0.2), 0.0, 6) } }
			}
		}
	}

	/** Runs as the player who hit or used the core. */
	val mine = dataPack.function("mine", directory = "core") {
		gain setTo perClick
		execute {
			ifCondition { frenzy.value greaterThanOrEqualTo 1 }
			run { gain *= Constants[3] }
		}
		function(economy.earn)
		formatter.format(gain, "gain")
		execute {
			anchored(Anchor.EYES)
			positioned(vec3(0.localPos, 0.localPos, 1.8.localPos))
			run { function(burst) }
		}
		animate(Entities.core, 2.7f, 2)
		squish.set(3)
		playSound(SoundEvents.Block.Stone.BREAK, PlaySoundMixer.MASTER, self(), vec3(), volume = 0.5, pitch = 1.3)
		function(hud.coreLabel)
	}

	/** Brings the core back to its normal size after a squash or a level-up pop. */
	val release = dataPack.function("release", directory = "core") {
		animate(Entities.core, 3f, 4)
	}
}
