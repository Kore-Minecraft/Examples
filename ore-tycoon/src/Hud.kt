import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.resources.BossBarArgument
import io.github.ayfri.kore.commands.bossBar
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.sidebar.sidebar
import io.github.ayfri.kore.scoreboard.*

/** Rewrites a text display, the game resolves its score and NBT components right away. */
private fun Function.setText(target: DataArgument, text: ChatComponents, condition: ExecuteCondition.() -> Unit) = execute {
	ifCondition(condition)
	run { data(target).modify("text", text.toNbtTag()) }
}

private fun title(title: String, color: Color) = textComponent(title, color) { bold = true }

class Hud(dataPack: DataPack, formatter: NumberFormatter) {
	val bossBar = BossBarArgument("progress", NAMESPACE)

	val sidebar = sidebar("ot.sidebar") {
		title("⛏ ORE TYCOON ⛏", Color.GOLD) { bold = true }
		emptyLine()
		line("Coins", Color.GRAY, hudText("coins", Color.GOLD))
		line("Per second", Color.GRAY, hudText("per_second", Color.GREEN))
		line("Per click", Color.GRAY, hudText("per_click", Color.AQUA))
		line("Lifetime", Color.GRAY, hudText("lifetime", Color.YELLOW))
		emptyLine()
		line("Level", Color.GRAY, level.text(Color.WHITE) + text("/${Level.FINAL.number}", Color.DARK_GRAY))
		line("Stars", Color.GRAY, textComponent("★ ", Color.LIGHT_PURPLE) + stars.text(Color.LIGHT_PURPLE)) {
			visibleIf { stars.value greaterThanOrEqualTo 1 }
		}
		line("FRENZY ×3", Color.LIGHT_PURPLE, frenzy.text(Color.LIGHT_PURPLE) + text("s", Color.LIGHT_PURPLE)) {
			visibleIf { frenzy.value greaterThanOrEqualTo 1 }
		}
		emptyLine()
		line("Made with Kore", Color.DARK_GRAY)
	}

	val coreLabel = dataPack.function("core_label", directory = "hud") {
		formatter.format(coins, "coins")
		fun counter() = hudText("coins", Color.GOLD, bold = true) + text(" $COIN", Color.GOLD) +
			text("${NEWLINE}+", Color.GREEN) + hudText("per_second", Color.GREEN) + text("/s", Color.GREEN)
		setText(Entities.coreLabel, counter()) { frenzy.value lessThanOrEqualTo 0 }
		setText(Entities.coreLabel, counter() + text("  ×3", Color.LIGHT_PURPLE) { bold = true }) { frenzy.value greaterThanOrEqualTo 1 }
	}

	private fun stationLabel(tier: Tier, price: ChatComponents) =
		title(tier.title.uppercase(), tier.color) +
			text("${NEWLINE}Owned ", Color.GRAY) + owned.getValue(tier).text(Color.WHITE) + text("/$MAX_OWNED", Color.DARK_GRAY) +
			text("${NEWLINE}+${tier.production.short()}/s each", Color.GREEN) + price

	private fun forgeLabel(pick: Pickaxe, price: ChatComponents) =
		title("PICKAXE FORGE", Color.AQUA) +
			text("${NEWLINE}${pick.title}", Color.WHITE) + text(" ${pick.flat}/click +${pick.percent}% of /s", Color.GRAY) + price

	private fun offer(key: String, affordable: Boolean, action: String) =
		textComponent("${NEWLINE}Price ", Color.GRAY) + coins(key, if (affordable) Color.GREEN else Color.RED) +
			text("${NEWLINE}Click to $action", if (affordable) Color.YELLOW else Color.DARK_GRAY)

	/** Every station, forge and altar hologram, with the price in green or red depending on the coins. */
	val labels = dataPack.function("labels", directory = "hud") {
		Tier.entries.forEach { tier ->
			val count = owned.getValue(tier)
			val cost = price.getValue(tier)
			setText(Entities.label(tier), stationLabel(tier, textComponent("${NEWLINE}MAXED OUT", Color.GOLD) { bold = true })) {
				count.value greaterThanOrEqualTo MAX_OWNED
			}
			listOf(true, false).forEach { affordable ->
				setText(Entities.label(tier), stationLabel(tier, offer("price.${tier.id}", affordable, "buy"))) {
					count.value lessThan MAX_OWNED
					if (affordable) coins.value greaterThanOrEqualTo cost.value else coins.value lessThan cost.value
				}
			}
		}

		Pickaxe.entries.forEach { pick ->
			val next = pick.next
			if (next == null) {
				setText(Entities.forgeLabel, forgeLabel(pick, textComponent("${NEWLINE}MAXED OUT", Color.GOLD) { bold = true })) {
					pickaxe.value equalTo pick.ordinal
				}
				return@forEach
			}
			listOf(true, false).forEach { affordable ->
				val upgrade = textComponent("${NEWLINE}Next: ${next.title}", Color.AQUA) + offer("price.pickaxe", affordable, "upgrade")
				setText(Entities.forgeLabel, forgeLabel(pick, upgrade)) {
					pickaxe.value equalTo pick.ordinal
					if (affordable) coins.value greaterThanOrEqualTo pickaxePrice.value else coins.value lessThan pickaxePrice.value
				}
			}
		}

		fun altar() = title("PRESTIGE ALTAR", Color.LIGHT_PURPLE) +
			text("${NEWLINE}★ ", Color.LIGHT_PURPLE) + stars.text(Color.WHITE) + text(" stars, +$STAR_BONUS_PERCENT% each", Color.GRAY)
		setText(Entities.altarLabel, altar() + text("${NEWLINE}Mine ${Level.FINAL.label} coins to unlock", Color.DARK_GRAY)) {
			level.value lessThan Level.FINAL.number
		}
		setText(Entities.altarLabel, altar() + text("${NEWLINE}Click to prestige!", Color.YELLOW) + text("${NEWLINE}Resets the run, keeps the stars", Color.GRAY)) {
			level.value equalTo Level.FINAL.number
		}
	}

	private val bars = Level.entries.associateWith { stage ->
		dataPack.function("bar_${stage.name.lowercase()}", directory = "hud") {
			val next = stage.next
			if (next == null) {
				bossBar(bossBar) { setMax(1) }
				bossBar(bossBar) { setValue(1) }
				bossBar(bossBar) { setColor(BossBarColor.PURPLE) }
				bossBar(bossBar) { setName(textComponent("★ FACTORY COMPLETE ★ ", Color.LIGHT_PURPLE) { bold = true } + text("Prestige at the altar!", Color.YELLOW)) }
				return@function
			}
			bossBar(bossBar) { setColor(BossBarColor.YELLOW) }
			bossBar(bossBar) { setMax(next.threshold - stage.threshold) }
			temp setTo lifetime
			temp -= Constants[stage.threshold]
			execute {
				storeResult { bossBarValue(bossBar.asString()) }
				run { scoreboard.players.get(temp.entity.asScoreHolder(), DATA) }
			}
			bossBar(bossBar) {
				setName(
					title("Level ${stage.number} ", Color.GOLD) + text("» ", Color.DARK_GRAY) + hudText("lifetime", Color.YELLOW) +
						text(" / ${next.label} mined", Color.GRAY) + text(" » next core: ${next.coreName}", Color.DARK_GRAY)
				)
			}
		}
	}

	val refresh = dataPack.function("refresh", directory = "hud") {
		formatter.format(lifetime, "lifetime")
		function(coreLabel)
		function(labels)
		sidebar.refresh()
		bars.forEach { (stage, bar) ->
			execute {
				ifCondition { level.value equalTo stage.number }
				run { function(bar) }
			}
		}
		bossBar(bossBar) { setPlayers(insidePlayers) }
	}
}
