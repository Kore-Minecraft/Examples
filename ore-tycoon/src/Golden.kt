import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.entityComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.commands.randomValue
import io.github.ayfri.kore.commands.returnIf
import io.github.ayfri.kore.commands.returnUnless
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.scoreboard.*
import io.github.ayfri.kore.utils.set

private const val GOLDEN_LIFETIME = 15
private const val FRENZY_SECONDS = 20
private const val LUCKY_SECONDS = 30

/** Stores a random integer of [range] in [target], through `execute store result ... run random value`. */
fun Function.roll(target: ScoreboardEntity, range: IntRange) = execute {
	storeResult { score(target.entity.asScoreHolder(), target.name) }
	run { randomValue(range) }
}

/** A glowing nugget pops up every minute or so: grab it before it vanishes for a lucky bonus or a production frenzy. */
class GoldenNugget(dataPack: DataPack, economy: Economy, formatter: NumberFormatter) {
	private val parts = allEntities { tag = "ot.golden" }

	private val spots = GOLDEN_SPOTS.mapIndexed { index, (x, z) ->
		dataPack.function("spot_$index", directory = "golden") {
			summonItem(Items.RAW_GOLD_BLOCK, vec3(center(x), FLOOR_Y + 2.0, center(z)), 0.6f, "ot.golden", "ot.spin") {
				this["Glowing"] = true
				this["glow_color_override"] = 0xFFD83D
			}
			summonInteraction(vec3(center(x), FLOOR_Y + 1.4, center(z)), 1.0f, 1.2f, "ot.golden", "ot.claim_golden")
		}
	}

	val spawn = dataPack.function("spawn", directory = "golden") {
		kill(parts)
		roll(random, spots.indices)
		spots.forEachIndexed { index, spot ->
			execute {
				ifCondition { random.value equalTo index }
				run { function(spot) }
			}
		}
		goldenLife.set(GOLDEN_LIFETIME)
		playSound(SoundEvents.Block.AmethystBlock.CHIME, PlaySoundMixer.MASTER, insidePlayers, vec3(), volume = 1.0, pitch = 1.4)
		announce(textComponent("A ", Color.YELLOW) + text("Golden Nugget", Color.GOLD) { bold = true } + text(" appeared, grab it quick!", Color.YELLOW))
	}

	private val despawn = dataPack.function("despawn", directory = "golden") {
		execute {
			at(Entities.golden)
			run { particle(Particles.POOF, vec3(), vec3(0.3, 0.3, 0.3), 0.02, 12) }
		}
		kill(parts)
		roll(goldenTimer, 45..90)
	}

	private val countdown = dataPack.function("countdown", directory = "golden") {
		goldenLife -= 1
		execute {
			ifCondition { goldenLife.value equalTo 0 }
			run { function(despawn) }
		}
	}

	private val startFrenzy = dataPack.function("frenzy", directory = "golden") {
		frenzy.set(FRENZY_SECONDS)
		title(self(), TitleLocation.TITLE, textComponent("FRENZY!", Color.LIGHT_PURPLE) { bold = true })
		title(self(), TitleLocation.SUBTITLE, textComponent("×3 coins for $FRENZY_SECONDS seconds", Color.YELLOW))
		announce(entityComponent(self()) + text(" started a ", Color.YELLOW) + text("×3 FRENZY", Color.LIGHT_PURPLE) { bold = true })
	}

	/** Runs as the player who grabbed the nugget: two chances in three of a lucky bonus, one of a frenzy. */
	val claim = dataPack.function("claim", directory = "golden") {
		execute {
			at(Entities.golden)
			run { particle(Particles.TOTEM_OF_UNDYING, vec3(), vec3(0.3, 0.3, 0.3), 0.5, 40) }
		}
		kill(parts)
		goldenLife.set(0)
		roll(goldenTimer, 45..90)
		playSound(SoundEvents.Entity.Player.LEVELUP, PlaySoundMixer.MASTER, self(), vec3(), pitch = 1.5)
		playSound(SoundEvents.Block.AmethystCluster.BREAK, PlaySoundMixer.MASTER, self())

		roll(random, 0..2)
		returnIf({ random.value equalTo 2 }) { function(startFrenzy) }
		gain setTo perSecond
		gain *= Constants[LUCKY_SECONDS]
		gain maxWith Constants[100]
		function(economy.earn)
		formatter.format(gain, "lucky")
		title(self(), TitleLocation.TITLE, textComponent("LUCKY!", Color.GOLD) { bold = true })
		title(self(), TitleLocation.SUBTITLE, textComponent("+", Color.YELLOW) + coins("lucky", Color.YELLOW))
		announce(entityComponent(self()) + text(" found a Golden Nugget worth ", Color.YELLOW) + coins("lucky"))
	}

	/** Counts down to the current nugget vanishing, or to the next one while someone is in the factory. */
	val second = dataPack.function("second", directory = "golden") {
		returnIf({ goldenLife.value greaterThanOrEqualTo 1 }) { function(countdown) }
		returnUnless(0) { entity(insidePlayers) }
		goldenTimer -= 1
		execute {
			ifCondition { goldenTimer.value lessThanOrEqualTo 0 }
			run { function(spawn) }
		}
	}
}
