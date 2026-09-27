import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.helpers.menus.menu
import io.github.ayfri.kore.arguments.chatcomponents.text as plain
import io.github.ayfri.kore.arguments.chatcomponents.textComponent

/** The in-game menu, also on the pause screen: every button works for players without operator rights. */
fun DataPack.mainMenu(players: Players) = menu("main", textComponent("⛏ Ore Tycoon ⛏", Color.GOLD) { bold = true }) {
	externalTitle = textComponent("⛏ Ore Tycoon", Color.GOLD)
	text(
		textComponent("An idle mining tycoon in the sky. ", Color.YELLOW) +
			plain("Punch the giant ore, buy drills that mine for you, evolve the core and mine a billion coins.", Color.GRAY)
	)
	button("▶ Play", Color.GREEN, "Fly to the factory, your spot here is saved") { function(players.play) }
	button("↩ Leave", Color.RED, "Go back where you clicked Play") { function(players.leave) }

	page("How to play", Color.YELLOW) {
		text(textComponent("Coins ", Color.GOLD) + plain("Hit or right-click the core in the middle to mine coins.", Color.GRAY))
		text(
			textComponent("Drills ", Color.AQUA) +
				plain("Each station sells a drill mining coins every second, even while you are away. Every copy costs 15% more.", Color.GRAY)
		)
		text(textComponent("Pickaxe forge ", Color.AQUA) + plain("Upgrades your clicks, the best pickaxes also add a share of the production.", Color.GRAY))
		text(textComponent("Golden nuggets ", Color.YELLOW) + plain("Grab them quick for 30 seconds of production or a ×3 frenzy.", Color.GRAY))
		text(textComponent("Levels ", Color.GREEN) + plain("The core evolves as you mine. Reach 1B lifetime coins to light the beacon.", Color.GRAY))
		text(
			textComponent("Prestige ", Color.LIGHT_PURPLE) +
				plain("Start over at the altar for a permanent +$STAR_BONUS_PERCENT% bonus per star.", Color.GRAY)
		)
		button("▶ Play", Color.GREEN) { function(players.play) }
	}

	page("About", Color.AQUA) {
		text(
			textComponent("Ore Tycoon is written in Kotlin with ", Color.GRAY) + plain("Kore", Color.GOLD) +
				plain(", a library generating Minecraft datapacks. No mods, no resource pack, pure vanilla.", Color.GRAY)
		)
		link("Kore website", "https://kore.ayfri.com", Color.GOLD)
		link("Source code", "https://github.com/Kore-Minecraft/Examples/tree/main/ore-tycoon", Color.AQUA)
	}

	pauseScreen = true
}
