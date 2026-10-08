import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.bossBar
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.forceLoad
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.teams
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.macro
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.helpers.menus.Menu
import io.github.ayfri.kore.scoreboard.*

/** Operator tools: `/function ore_tycoon:admin/<name>`. */
class Admin(
	dataPack: DataPack,
	economy: Economy,
	factory: Factory,
	hud: Hud,
	progress: Progress,
	golden: GoldenNugget,
	players: Players,
	menu: Menu,
) {
	/** `/function ore_tycoon:admin/give {amount:1000000}`, capped at 100M per call. */
	val give = dataPack.function("give", directory = "admin") {
		addLine("scoreboard players set ${gain.entity.asScoreHolder().asString()} $DATA ${macro("amount")}")
		function(economy.earn)
		function(hud.refresh)
		tellraw(self(), prefix() + text("Added ", Color.GREEN) + gain.text(Color.GOLD) + text(" coins.", Color.GREEN))
	}

	/** Its blocks and entities are placed at absolute coordinates, so it runs in the overworld wherever the operator is. */
	val reset = dataPack.function("reset", directory = "admin") {
		stars.set(0)
		execute {
			inDimension(Dimensions.OVERWORLD)
			run { function(progress.resetRun) }
		}
		tellraw(allPlayers(), prefix() + text("Progress reset, stars included.", Color.RED))
	}

	val nugget = dataPack.function("golden_nugget", directory = "admin") {
		execute {
			inDimension(Dimensions.OVERWORLD)
			run { function(golden.spawn) }
		}
	}

	/** Rebuilds the blocks and entities, keeping the progress, from any dimension: the island chunks are forceloaded first. */
	val rebuild = dataPack.function("rebuild", directory = "admin") {
		function(factory.requestBuild)
		tellraw(self(), prefix() + text("Rebuilding the factory.", Color.GREEN))
	}

	private val finishUninstall = dataPack.function("uninstall_finish", directory = "admin") {
		function(factory.clear)
		forceLoad { remove(factory.area.first, factory.area.second) }
		listOf(DATA, AGE, PLAYER_ID, hud.sidebar.objective, menu.objective).forEach { scoreboard.objectives.remove(it) }
		bossBar(hud.bossBar) { remove() }
		teams { remove(TEAM) }
		listOf("args", "hud", "players").forEach { data(STORAGE).remove(it) }
		tellraw(
			allPlayers(),
			prefix() + text("The factory and its data are gone. Remove the datapack file, or it comes back on the next /reload.", Color.YELLOW),
		)
	}

	/** Sends everyone home, loads the factory chunks so its entities can be removed, then deletes everything two seconds later. */
	val uninstall = dataPack.function("uninstall", directory = "admin") {
		execute {
			asTarget(insidePlayers)
			run { function(players.leave) }
		}
		tag(allPlayers()) { remove(WAITING) }
		execute {
			inDimension(Dimensions.OVERWORLD)
			run { forceLoad { add(factory.area.first, factory.area.second) } }
		}
		schedule(finishUninstall).replace(40.ticks)
		tellraw(allPlayers(), prefix() + text("Uninstalling...", Color.YELLOW))
	}
}
