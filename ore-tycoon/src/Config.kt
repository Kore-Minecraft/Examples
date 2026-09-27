import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.colors.color
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

const val NAMESPACE = "ore_tycoon"

/** The factory floats in the sky above x = 0, z = 0, far above any natural terrain. Players walk on `FLOOR_Y + 1`. */
const val FLOOR_Y = 279
const val CORE_Y = 282.5
const val ISLAND_RADIUS = 16
const val STATION_RADIUS = 9

const val MAX_COINS = 2_000_000_000
const val MAX_GAIN = 100_000_000
const val MAX_OWNED = 25
const val MAX_STARS = 20
const val STAR_BONUS_PERCENT = 25
const val PRICE_GROWTH = 1.15

/** Center of the block at ([x], [z]), where entities of a station stand. */
fun center(x: Int) = x + 0.5

/** Block coordinates of the point at [radius] blocks from the core, [degrees] clockwise from north. */
fun polar(radius: Double, degrees: Double): Pair<Int, Int> {
	val angle = degrees * PI / 180
	return (radius * sin(angle)).roundToInt() to (-radius * cos(angle)).roundToInt()
}

/** A drill station: buying one adds [production] coins per second, each copy costing [PRICE_GROWTH] times the last. */
enum class Tier(
	val title: String,
	val color: RGB,
	val machine: ItemArgument,
	val product: ItemArgument,
	val pedestal: BlockArgument,
	val baseCost: Int,
	val production: Int,
) {
	COAL("Coal Drill", color("#b4b4b4"), Items.COAL_ORE, Items.COAL, Blocks.COAL_BLOCK, 15, 1),
	COPPER("Copper Drill", color("#f09a6b"), Items.COPPER_ORE, Items.RAW_COPPER, Blocks.RAW_COPPER_BLOCK, 110, 6),
	IRON("Iron Drill", color("#e8d2bf"), Items.IRON_ORE, Items.RAW_IRON, Blocks.RAW_IRON_BLOCK, 1_200, 40),
	GOLD("Gold Drill", color("#ffd83d"), Items.GOLD_ORE, Items.RAW_GOLD, Blocks.RAW_GOLD_BLOCK, 13_000, 220),
	DIAMOND("Diamond Drill", color("#62f0f5"), Items.DIAMOND_ORE, Items.DIAMOND, Blocks.DIAMOND_BLOCK, 140_000, 1_300),
	EMERALD("Emerald Drill", color("#4ce38a"), Items.EMERALD_ORE, Items.EMERALD, Blocks.EMERALD_BLOCK, 1_600_000, 8_000),
	NETHERITE("Netherite Forge", color("#c792ff"), Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP, Blocks.NETHERITE_BLOCK, 20_000_000, 50_000);

	val id = name.lowercase()

	/** Price of the next copy for each owned count, computed once at build time instead of with runtime float math. */
	val prices = List(MAX_OWNED) { (baseCost * PRICE_GROWTH.pow(it)).roundToInt() }

	/** Stations sit on a ring around the core, cheapest on the left of the arrival point, clockwise. */
	val position = polar(STATION_RADIUS.toDouble(), -135.0 + 45.0 * ordinal)
	val x get() = position.first
	val z get() = position.second
}

/** Click upgrades: each click mines [flat] coins plus [percent] % of the production per second. */
enum class Pickaxe(val title: String, val item: ItemArgument, val cost: Int, val flat: Int, val percent: Int) {
	WOODEN("Wooden Pickaxe", Items.WOODEN_PICKAXE, 0, 1, 0),
	STONE("Stone Pickaxe", Items.STONE_PICKAXE, 150, 3, 1),
	IRON("Iron Pickaxe", Items.IRON_PICKAXE, 3_000, 10, 2),
	DIAMOND("Diamond Pickaxe", Items.DIAMOND_PICKAXE, 60_000, 40, 4),
	NETHERITE("Netherite Pickaxe", Items.NETHERITE_PICKAXE, 1_500_000, 200, 8);

	val next get() = entries.getOrNull(ordinal + 1)
}

/** Lifetime milestones: reaching [threshold] coins evolves the core into [core], the last one wins the game. */
enum class Level(val threshold: Int, val core: ItemArgument, val coreName: String, val label: String) {
	STONE(0, Items.STONE, "Stone", "0"),
	COAL(1_000, Items.COAL_ORE, "Coal Ore", "1K"),
	IRON(10_000, Items.IRON_ORE, "Iron Ore", "10K"),
	GOLD(100_000, Items.GOLD_ORE, "Gold Ore", "100K"),
	DIAMOND(1_000_000, Items.DIAMOND_ORE, "Diamond Ore", "1M"),
	EMERALD(10_000_000, Items.EMERALD_ORE, "Emerald Ore", "10M"),
	DEBRIS(100_000_000, Items.ANCIENT_DEBRIS, "Ancient Debris", "100M"),
	BEACON(1_000_000_000, Items.BEACON, "Beacon", "1B");

	val number = ordinal + 1
	val next get() = entries.getOrNull(ordinal + 1)

	companion object {
		val FINAL = entries.last()
	}
}

/** Where players land, facing the core. */
val ARRIVAL = center(0) to 13.5

/** The pickaxe forge and the prestige altar flank the arrival point. */
val FORGE = polar(12.0, -160.0)
val ALTAR = polar(12.0, 160.0)

/** Golden nuggets appear between the stations or next to the core. */
val GOLDEN_SPOTS = listOf(-112.5, -67.5, -22.5, 22.5, 67.5, 112.5).map { polar(11.0, it) } + listOf(-4 to 4, 4 to 4)
