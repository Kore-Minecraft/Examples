import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.vec2
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.gamemode
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.commands.returnIf
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.spreadPlayers
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.teams
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.commands.tp
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.macro
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.scoreboard.*

/** Limits a selector to the factory volume. Volume selectors only see the executing dimension, the overworld here. */
private fun SelectorArguments.inFactory() {
	x = -20.0
	y = FLOOR_Y - 16.0
	z = -20.0
	dx = 40.0
	dy = 40.0
	dz = 40.0
}

private const val WAS_SURVIVAL = "ot.was_survival"

/**
 * Brings players to the factory and back. Each return point is saved in storage under a per-player id:
 * commands can't index storage with a score, so macros paste the id into the NBT path.
 */
class Players(dataPack: DataPack, factory: Factory, hud: Hud) {
	private val Function.entry get() = "players[{id:${macro("id")}}]"

	private val saveReturn = dataPack.function("save_return", directory = "player") {
		data(STORAGE).remove(entry)
		addLine("data modify storage ${STORAGE.asString()} players append value {id:${macro("id")}}")
		data(STORAGE).modify("$entry.pos", self(), "Pos")
		data(STORAGE).modify("$entry.rotation", self(), "Rotation")
		data(STORAGE).modify("$entry.dimension", self(), "Dimension")
	}

	private val loadReturn = dataPack.function("load_return", directory = "player") {
		data(STORAGE).modify("args.return", STORAGE, entry)
		data(STORAGE).remove(entry)
	}

	private val teleportBack = dataPack.function("teleport_back", directory = "player") {
		addLine("execute in ${macro("dimension")} run tp @s ${macro("x")} ${macro("y")} ${macro("z")} ${macro("yaw")} ${macro("pitch")}")
	}

	/** Copies the player id into `args.id`, the macro argument of the return point functions. */
	private fun Function.idArgument() = execute {
		storeResult { storage(STORAGE, "args.id", DataType.INT, 1.0) }
		run { scoreboard.players.get(self(), PLAYER_ID) }
	}

	/** Runs as any player standing in the factory, however they got there: HUD, team and a full belly. */
	val join = dataPack.function("join", directory = "player") {
		tag(self()) { add(INSIDE) }
		teams { join(TEAM, self()) }
		effect(self()) { giveInfinite(Effects.SATURATION, 0, true) }
		title(self(), TitleLocation.TITLE, textComponent("ORE TYCOON", Color.GOLD) { bold = true })
		title(self(), TitleLocation.SUBTITLE, textComponent("Punch the core to mine coins", Color.YELLOW))
		playSound(SoundEvents.Block.Beacon.ACTIVATE, PlaySoundMixer.MASTER, self(), vec3(), pitch = 1.4)
		function(hud.refresh)
	}

	/** Runs as a player who is no longer in the factory, whatever the reason: leave, death, teleport. */
	val cleanup = dataPack.function("cleanup", directory = "player") {
		tag(self()) { remove(INSIDE) }
		teams { leave(self()) }
		effect(self()) { clear(Effects.SATURATION) }
		execute {
			ifCondition { entity(self { tag = WAS_SURVIVAL }) }
			run { gamemode(Gamemode.SURVIVAL, self()) }
		}
		tag(self()) { remove(WAS_SURVIVAL) }
	}

	val play = dataPack.function("play", directory = "player") {
		execute {
			unlessCondition { score(self(), PLAYER_ID, rangeOrIntStart(1)) }
			run { nextPlayerId += 1 }
		}
		execute {
			unlessCondition { score(self(), PLAYER_ID, rangeOrIntStart(1)) }
			run { scoreboard.players.operation(self(), PLAYER_ID, Operation.SET, nextPlayerId.entity.asScoreHolder(), DATA) }
		}
		execute {
			unlessCondition { entity(self { tag = INSIDE }) }
			run { idArgument() }
		}
		execute {
			unlessCondition { entity(self { tag = INSIDE }) }
			run { function(saveReturn, arguments = STORAGE, path = "args") }
		}
		execute {
			ifCondition { entity(self { gamemode = Gamemode.SURVIVAL }) }
			run { tag(self()) { add(WAS_SURVIVAL) } }
		}
		execute {
			ifCondition { entity(self { tag = WAS_SURVIVAL }) }
			run { gamemode(Gamemode.ADVENTURE, self()) }
		}
		execute {
			inDimension(Dimensions.OVERWORLD)
			run { tp(self(), vec3(ARRIVAL.first, FLOOR_Y + 1.0, ARRIVAL.second), rotation(180, 0)) }
		}
		execute {
			unlessCondition { built.value equalTo 1 }
			run { schedule(factory.build).replace(5.ticks) }
		}
		schedule(factory.sync).replace(20.ticks)
		function(join)
	}

	/** Sends the player back to where they clicked Play, or near the world origin under the island if nothing is saved. */
	val leave = dataPack.function("leave", directory = "player") {
		returnIf({ inverted { entity(self { tag = INSIDE }) } }) {
			actionBar(textComponent("You are not in the factory.", Color.RED))
		}
		function(cleanup)
		idArgument()
		data(STORAGE).remove("args.return")
		function(loadReturn, arguments = STORAGE, path = "args")
		returnIf({ inverted { data(STORAGE, "args.return.pos") } }) {
			spreadPlayers(vec2(0, 0), 0.0, 64.0, FLOOR_Y - 40, false, self())
		}
		listOf("x", "y", "z").forEachIndexed { index, axis -> data(STORAGE).modify("args.$axis", STORAGE, "args.return.pos[$index]") }
		data(STORAGE).modify("args.yaw", STORAGE, "args.return.rotation[0]")
		data(STORAGE).modify("args.pitch", STORAGE, "args.return.rotation[1]")
		data(STORAGE).modify("args.dimension", STORAGE, "args.return.dimension")
		function(teleportBack, arguments = STORAGE, path = "args")
		actionBar(textComponent("See you soon! Your drills keep mining while you are away.", Color.GOLD))
	}

	/** Keeps the `ot.in` tag in line with where players really are. */
	val second = dataPack.function("second", directory = "player") {
		execute {
			asTarget(allPlayers { tag = INSIDE })
			unlessCondition { entity(self { inFactory() }) }
			run { function(cleanup) }
		}
		execute {
			asTarget(allPlayers {
				tag = "!$INSIDE"
				inFactory()
			})
			run { function(join) }
		}
	}
}
