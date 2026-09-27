import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.entityComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.commands.returnIf
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.helpers.vfx.Shape
import io.github.ayfri.kore.helpers.vfx.drawShape
import io.github.ayfri.kore.scoreboard.*
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set

private val rocketColors = Tier.entries.map { it.color }.map { (it.red shl 16) or (it.green shl 8) or it.blue }.toIntArray()

/** A firework rocket bursting in every drill color one second after launch. */
private fun rocketNbt(shape: String) = nbt {
	this["LifeTime"] = 20
	this["FireworksItem"] = nbt {
		this["id"] = Items.FIREWORK_ROCKET.asId()
		this["count"] = 1
		this["components"] = nbt {
			this["minecraft:fireworks"] = nbt {
				this["flight_duration"] = 1.toByte()
				this["explosions"] = nbtListOf(nbt {
					this["shape"] = shape
					this["colors"] = rocketColors
					this["fade_colors"] = intArrayOf(0xFFFFFF)
					this["has_trail"] = true
					this["has_twinkle"] = true
				})
			}
		}
	}
}

class Progress(dataPack: DataPack, economy: Economy, factory: Factory, hud: Hud) {
	private val helix = dataPack.drawShape("level_helix") {
		shape = Shape.HELIX
		particle = Particles.TOTEM_OF_UNDYING
		radius = 2.3
		height = 5.0
		turns = 3
		points = 60
	}

	/** Back to square one, except for the prestige stars. */
	val resetRun = dataPack.function("reset_run", directory = "progress") {
		coins.set(0)
		lifetime.set(0)
		Tier.entries.forEach { owned.getValue(it).set(0) }
		pickaxe.set(0)
		level.set(1)
		frenzy.set(0)
		goldenTimer.set(60)
		goldenLife.set(0)
		kill(allEntities { tag = "ot.golden" })
		function(economy.prices)
		function(economy.recalculate)
		function(factory.sync)
		function(hud.refresh)
	}

	private val victory = dataPack.function("victory", directory = "progress") {
		listOf(-8 to -8, -8 to 8, 8 to -8, 8 to 8, 0 to -12, -12 to 0, 12 to 0).forEachIndexed { index, (x, z) ->
			summon(EntityTypes.FIREWORK_ROCKET, vec3(center(x), FLOOR_Y + 2.0, center(z)), rocketNbt(if (index % 2 == 0) "large_ball" else "star"))
		}
		playSound(SoundEvents.Block.Beacon.ACTIVATE, PlaySoundMixer.MASTER, insidePlayers)
		tellraw(
			allPlayers(),
			prefix() + text("The factory mined its first billion coins! ", Color.GREEN) +
				text("Prestige at the altar for a permanent +$STAR_BONUS_PERCENT% bonus.", Color.LIGHT_PURPLE),
		)
	}

	private val reach = Level.entries.drop(1).associateWith { stage ->
		dataPack.function("level_${stage.name.lowercase()}", directory = "progress") {
			level.set(stage.number)
			function(factory.sync)
			animate(Entities.core, 3.8f, 5)
			squish.set(7)
			execute {
				positioned(vec3(center(0), FLOOR_Y + 1.0, center(0)))
				run { function(helix) }
			}
			title(insidePlayers, TitleLocation.TITLE, textComponent("LEVEL ${stage.number}", Color.GOLD) { bold = true })
			title(insidePlayers, TitleLocation.SUBTITLE, textComponent("The core turned into ${stage.coreName}", Color.YELLOW))
			playSound(SoundEvents.Ui.Toast.CHALLENGE_COMPLETE, PlaySoundMixer.MASTER, insidePlayers)
			announce(textComponent("Level ${stage.number} reached, ", Color.GREEN) + text("the core turned into ${stage.coreName}!", Color.YELLOW))
			if (stage == Level.FINAL) function(victory)
		}
	}

	/** One level per call, so a big jump still plays every level up, one per second. */
	val checkLevel = dataPack.function("check_level", directory = "progress") {
		Level.entries.drop(1).forEach { stage ->
			returnIf({
				level.value lessThan stage.number
				lifetime.value greaterThanOrEqualTo stage.threshold
			}) { function(reach.getValue(stage)) }
		}
	}

	/** Runs as the player who used the altar. */
	val prestige = dataPack.function("prestige", directory = "progress") {
		returnIf({ level.value lessThan Level.FINAL.number }) {
			actionBar(textComponent("Mine ${Level.FINAL.label} coins in this run to unlock the prestige", Color.RED))
		}
		returnIf({ stars.value greaterThanOrEqualTo MAX_STARS }) {
			actionBar(textComponent("You reached the maximum of $MAX_STARS stars!", Color.GOLD))
		}
		stars += 1
		function(resetRun)

		val (altarX, altarZ) = ALTAR
		particle(Particles.TOTEM_OF_UNDYING, vec3(center(altarX), FLOOR_Y + 2.5, center(altarZ)), vec3(0.5, 1.0, 0.5), 0.4, 80)
		playSound(SoundEvents.Block.Beacon.POWER_SELECT, PlaySoundMixer.MASTER, insidePlayers)
		title(insidePlayers, TitleLocation.TITLE, textComponent("★ PRESTIGE ★", Color.LIGHT_PURPLE) { bold = true })
		title(insidePlayers, TitleLocation.SUBTITLE, textComponent("+$STAR_BONUS_PERCENT% production and clicks, forever", Color.YELLOW))
		announce(entityComponent(self()) + text(" prestiged! Stars: ", Color.LIGHT_PURPLE) + stars.text(Color.WHITE))
	}
}
