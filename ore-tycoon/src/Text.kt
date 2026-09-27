import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.nbtComponent
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity

const val COIN = "⛃"

/** The SNBT escape of a line break: a raw newline in a text would split the function line in two. */
const val NEWLINE = "\\n"

val insidePlayers = allPlayers { tag = INSIDE }

/** A function, not a value: `ChatComponents.plus` appends to its left operand, a shared prefix would pile up every message. */
fun prefix() = textComponent("⛏ Ore Tycoon ", Color.GOLD) { bold = true } + text("» ", Color.DARK_GRAY)

/** A number formatted by [NumberFormatter], read back from [STORAGE] when the text is resolved. */
fun hudText(key: String, color: Color? = null, bold: Boolean? = null) = nbtComponent("hud.$key", STORAGE) {
	interpret = true
	this.color = color
	this.bold = bold
}

/** The live value of a score, resolved when the text is displayed. */
fun ScoreboardEntity.text(color: Color? = null) = scoreComponent(name, entity.asScoreHolder()) { this.color = color }

fun coins(key: String, color: Color = Color.GOLD) = hudText(key, color) + text(" $COIN", Color.GOLD)

fun Function.actionBar(message: ChatComponents) = title(self(), TitleLocation.ACTIONBAR, message)

fun Function.announce(message: ChatComponents) = tellraw(insidePlayers, prefix() + message)
