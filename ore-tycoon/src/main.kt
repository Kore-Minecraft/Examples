import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.actions.runCommand
import io.github.ayfri.kore.arguments.actions.showDialog
import io.github.ayfri.kore.arguments.chatcomponents.clickEvent
import io.github.ayfri.kore.arguments.chatcomponents.hoverEvent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.relativeRot
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.BossBarStyle
import io.github.ayfri.kore.commands.CollisionRule
import io.github.ayfri.kore.commands.bossBar
import io.github.ayfri.kore.commands.bossBars
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.commands.returnUnless
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.teams
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.tp
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.iconPath
import io.github.ayfri.kore.pack.maxFormat
import io.github.ayfri.kore.pack.minFormat
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.scoreboard.*

val isGitHubCI get() = System.getenv("CI") != null

fun main() {
	dataPack(NAMESPACE) {
		configuration {
			prettyPrint = !isGitHubCI
		}

		iconPath("ore-tycoon/pack.png")

		pack {
			minFormat(107, 1)
			maxFormat(107)
			description = textComponent("Ore Tycoon", Color.GOLD) + text(", an idle mining tycoon made with Kore", Color.GRAY)
		}

		val formatter = NumberFormatter(this)
		val economy = Economy(this, formatter)
		val factory = Factory(this)
		val hud = Hud(this, formatter)
		val shop = Shop(this, economy, formatter, factory, hud)
		val progress = Progress(this, economy, factory, hud)
		val golden = GoldenNugget(this, economy, formatter)
		val core = Core(this, economy, formatter, hud)
		val flyers = Flyers(this)
		val players = Players(this, factory, hud)
		val menu = mainMenu(players)
		Admin(this, economy, factory, hud, progress, golden, players, menu)

		clickDetection(
			Tier.entries.associate { "ot.buy.${it.id}" to shop.buy.getValue(it) } + mapOf(
				"ot.buy.pickaxe" to shop.upgradePickaxe,
				"ot.claim_golden" to golden.claim,
				"ot.prestige" to progress.prestige,
				"ot.use_core" to core.mine,
			)
		)

		/** Dialogs only register when the world loads, this link fails to load after a plain `/reload`, so it lives apart. */
		val menuLink = function("menu_link", directory = "player") {
			tellraw(
				self(),
				textComponent("[☰ Open the menu]", Color.AQUA) {
					clickEvent { showDialog(menu.dialog) }
					hoverEvent("Also on the pause screen", Color.GRAY)
				} + text(" How to play, leave the factory, credits.", Color.GRAY),
			)
		}

		/** Play is the menu's first button: `/trigger <menu objective> set 1` presses it, and triggers work without operator rights. */
		val welcome = function("welcome", directory = "player") {
			tag(self()) { add("ot.welcomed") }
			tellraw(
				self(),
				prefix() + text("Punch ores, buy drills, get rich. ", Color.YELLOW) +
					textComponent("[▶ Play]", Color.GREEN) {
						bold = true
						clickEvent { runCommand("/trigger ${menu.objective} set 1") }
						hoverEvent("Fly to the factory", Color.GRAY)
					},
			)
			function(menuLink)
		}

		val popText = tagged(EntityTypes.TEXT_DISPLAY, "ot.pop", single = false)
		val newPopText = tagged(EntityTypes.TEXT_DISPLAY, "ot.pop_new", single = false)

		/** Every tick while the factory exists: short animations driven by the tick counter. */
		val animations = function("animations") {
			execute {
				ifCondition { squish.value equalTo 1 }
				run { function(core.release) }
			}
			execute {
				ifCondition { squish.value greaterThanOrEqualTo 1 }
				run { squish -= 1 }
			}

			execute {
				asTarget(newPopText)
				run { animate(self(), 0.9f, 12, lift = 1.2f) }
			}
			tag(newPopText) { remove("ot.pop_new") }
			scoreboard.players.add(popText, AGE, 1)
			kill(allEntities {
				type = EntityTypes.TEXT_DISPLAY
				tag = "ot.pop"
				scores { score(AGE) greaterThanOrEqualTo 13 }
			})

			mapOf(1 to flyers.rise, 10 to flyers.dive, 19 to flyers.land).forEach { (at, stage) ->
				execute {
					ifCondition { clock.value equalTo at }
					run { function(stage) }
				}
			}

			execute {
				at(Entities.golden)
				run { particle(Particles.WAX_ON, vec3(), vec3(0.35, 0.35, 0.35), 0.0, 1) }
			}
		}

		val second = function("second") {
			gain setTo perSecond
			execute {
				ifCondition { frenzy.value greaterThanOrEqualTo 1 }
				run { gain *= Constants[3] }
			}
			function(economy.earn)
			execute {
				ifCondition { frenzy.value greaterThanOrEqualTo 1 }
				run { frenzy -= 1 }
			}
			function(progress.checkLevel)
			function(golden.second)
			function(players.second)
			execute {
				asTarget(allPlayers { tag = "!ot.welcomed" })
				run { function(welcome) }
			}

			returnUnless(0) { entity(insidePlayers) }
			execute {
				asTarget(tagged(EntityTypes.ITEM_DISPLAY, "ot.spin", single = false))
				at(self())
				run { tp(self(), vec3(), rotation(90.relativeRot, 0.relativeRot)) }
			}
			execute {
				asTarget(tagged(EntityTypes.ITEM_DISPLAY, "ot.spin_slow", single = false))
				at(self())
				run { tp(self(), vec3(), rotation(30.relativeRot, 0.relativeRot)) }
			}
			function(flyers.launch)
			function(hud.refresh)
		}

		tick("tick") {
			clock += 1
			execute {
				ifCondition { clock.value greaterThanOrEqualTo 20 }
				run { clock.set(0) }
			}
			execute {
				ifCondition { clock.value equalTo 0 }
				run { function(second) }
			}
			execute {
				ifCondition { built.value equalTo 1 }
				run { function(animations) }
			}
		}

		val install = function("install") {
			stars.set(0)
			nextPlayerId.set(0)
			function(progress.resetRun)
			initialized.set(1)
		}

		/** Declared last: every [Constants] entry is registered by now. */
		load("load") {
			listOf(DATA, AGE, PLAYER_ID).forEach { scoreboard.objectives.add(it, ScoreboardCriteria.DUMMY) }
			Constants.setAll()
			teams { add(TEAM, textComponent("Ore Tycoon", Color.GOLD)) }
			teams { modify(TEAM) { color(FormattingColor.GOLD) } }
			teams { modify(TEAM) { collisionRule(CollisionRule.NEVER) } }
			teams { modify(TEAM) { friendlyFire(false) } }
			bossBars.add("progress", NAMESPACE, textComponent("Ore Tycoon", Color.GOLD))
			bossBar(hud.bossBar) { setStyle(BossBarStyle.NOTCHED_10) }
			execute {
				unlessCondition { initialized.value equalTo 1 }
				run { function(install) }
			}
			function(economy.prices)
			function(economy.recalculate)
			hud.sidebar.create(DisplaySlots.sidebarTeam(FormattingColor.GOLD))
			function(hud.refresh)
			tag(allPlayers()) { remove("ot.welcomed") }
		}

		when {
			isGitHubCI -> generateZip()
			else -> generate()
		}
	}
}
