import io.github.ayfri.kore.arguments.scores.ExecuteScore
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.entities.fakePlayer
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.scoreboard.scoreboard

/** Every global value lives on its own `#name` fake player of this objective, hidden from any sidebar. */
const val DATA = "ot.data"

/** Per-entity lifetime in ticks, for the short-lived floating texts. */
const val AGE = "ot.age"

/** Per-player numeric id, the key of their saved return point. */
const val PLAYER_ID = "ot.pid"

/** Scoreboard tag carried by every entity of the pack, `kill @e[tag=ot.entity]` removes the whole factory. */
const val ENTITY = "ot.entity"

/** Players currently inside the factory. */
const val INSIDE = "ot.in"

const val TEAM = "ot.players"

/** All NBT of the pack: `hud` holds formatted numbers, `args` macro arguments, `players` return points. */
val STORAGE = storage("data", NAMESPACE)

fun global(name: String) = scoreboard(DATA, fakePlayer(name))

/** The score as an operand of `execute if score`. */
val ScoreboardEntity.value get() = ExecuteScore(entity.asScoreHolder(), name)

val coins = global("coins")
val lifetime = global("lifetime")
val perSecond = global("per_second")
val perClick = global("per_click")
val pickaxe = global("pickaxe")
val level = global("level")
val stars = global("stars")
val frenzy = global("frenzy")
val goldenTimer = global("golden_timer")
val goldenLife = global("golden_life")
val squish = global("squish")
val clock = global("clock")
val built = global("built")
val initialized = global("initialized")
val nextPlayerId = global("next_player_id")
val gain = global("gain")
val multiplier = global("multiplier")
val temp = global("temp")
val random = global("random")

val owned = Tier.entries.associateWith { global("owned.${it.id}") }
val price = Tier.entries.associateWith { global("price.${it.id}") }
val pickaxePrice = global("price.pickaxe")
