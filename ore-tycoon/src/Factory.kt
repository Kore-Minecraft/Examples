import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.fill
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.returnRun
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.setBlock
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.scoreboard.set
import io.github.ayfri.kore.utils.set
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.random.Random

private const val ISLAND_DEPTH = 12
private const val REACH = ISLAND_RADIUS + 3

private fun distance(x: Int, z: Int) = hypot(x.toDouble(), z.toDouble())

/** Deterministic noise in [0, 1): the island looks hand-made but is identical on every build. */
private fun noise(x: Int, z: Int, salt: Int) = Random(x * 73_856_093 xor z * 19_349_663 xor salt * 83_492_791).nextDouble()

private fun isPad(x: Int, z: Int) =
	(Tier.entries.map { it.position } + FORGE + ALTAR).any { (px, pz) -> x in px - 1..px + 1 && z in pz - 1..pz + 1 }

/** Copper grates from the core to each station, under the path of the flying ores. */
private fun isSpoke(x: Int, z: Int) = Tier.entries.any { tier ->
	val length = distance(tier.x, tier.z)
	val along = (x * tier.x + z * tier.z) / length
	val across = distance(x, z).pow(2) - along * along
	along in 4.6..length - 1.6 && across <= 0.3
}

private fun floorBlock(x: Int, z: Int): BlockArgument? {
	val d = distance(x, z)
	return when {
		d <= 1.5 -> Blocks.GOLD_BLOCK
		d <= 3.6 -> Blocks.POLISHED_BLACKSTONE_BRICKS
		d <= 4.6 -> Blocks.WAXED_CUT_COPPER
		isPad(x, z) -> Blocks.CHISELED_POLISHED_BLACKSTONE
		isSpoke(x, z) -> Blocks.WAXED_COPPER_GRATE
		d <= 12.9 -> if ((x + z).mod(2) == 0) Blocks.DEEPSLATE_TILES else Blocks.POLISHED_DEEPSLATE
		d <= 13.9 || x in -1..1 && z > 0 -> Blocks.POLISHED_BLACKSTONE_BRICKS
		d <= ISLAND_RADIUS + 0.5 -> Blocks.GRASS_BLOCK
		else -> null
	}
}

private val shallowOres = listOf(Blocks.COAL_ORE, Blocks.COPPER_ORE, Blocks.IRON_ORE)
private val deepOres = listOf(Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE)

/** The rock under the floor: a jagged cone of dirt, stone then deepslate, sprinkled with ores. */
private fun islandBlock(x: Int, z: Int, depth: Int): BlockArgument? {
	val radius = (ISLAND_RADIUS + 0.5) * (1 - (depth / (ISLAND_DEPTH + 1.0)).pow(1.6)) + (noise(x, z, depth) - 0.5) * 2.4
	val d = distance(x, z)
	val deep = depth >= ISLAND_DEPTH / 2 + 1
	val roll = noise(x, z, depth + 100)
	return when {
		d > radius -> null
		depth == 1 && x in -1..1 && z in -1..1 -> Blocks.IRON_BLOCK
		depth <= 2 && d > radius - 2.5 -> Blocks.DIRT
		roll < 0.07 -> (if (deep) deepOres else shallowOres)[(roll * 1000).toInt() % 3]
		deep -> Blocks.DEEPSLATE
		else -> Blocks.STONE
	}
}

private val flowers = listOf(
	Blocks.SHORT_GRASS, Blocks.SHORT_GRASS, Blocks.SHORT_GRASS, Blocks.POPPY, Blocks.DANDELION, Blocks.CORNFLOWER,
	Blocks.AZURE_BLUET, Blocks.OXEYE_DAISY, Blocks.FLOWERING_AZALEA,
)

/** Emits one `fill` per horizontal run of identical blocks: a whole disc costs a few dozen commands instead of hundreds of `setblock`. */
fun Function.layer(y: Int, blockAt: (x: Int, z: Int) -> BlockArgument?) {
	for (z in -REACH..REACH) {
		var start = -REACH
		var current: BlockArgument? = null
		for (x in -REACH..REACH + 1) {
			val block = if (x <= REACH) blockAt(x, z) else null
			if (block?.asString() == current?.asString()) continue
			current?.let { fill(vec3(start, y, z), vec3(x - 1, y, z), it) }
			start = x
			current = block
		}
	}
}

class Factory(dataPack: DataPack) {
	/** Removes every block and entity of the factory, the blocks in two fills to stay under the 32768 blocks limit. */
	val clear = dataPack.function("clear", directory = "factory") {
		kill(Entities.all)
		fill(vec3(-REACH, FLOOR_Y - ISLAND_DEPTH - 2, -REACH), vec3(REACH, FLOOR_Y - 2, REACH), Blocks.AIR)
		fill(vec3(-REACH, FLOOR_Y - 1, -REACH), vec3(REACH, FLOOR_Y + 8, REACH), Blocks.AIR)
	}

	private val blocks = dataPack.function("blocks", directory = "factory") {
		for (depth in ISLAND_DEPTH downTo 1) layer(FLOOR_Y - depth) { x, z -> islandBlock(x, z, depth) }
		layer(FLOOR_Y, ::floorBlock)
		for (y in FLOOR_Y + 1..FLOOR_Y + 4) layer(y) { x, z -> Blocks.BARRIER.takeIf { distance(x, z) in 16.6..18.2 } }

		for (x in -REACH..REACH) for (z in -REACH..REACH) {
			val d = distance(x, z)
			if (d !in 14.0..16.3 || x in -1..1 && z > 0 || noise(x, z, 7) > 0.4) continue
			setBlock(vec3(x, FLOOR_Y + 1, z), flowers[(noise(x, z, 8) * flowers.size).toInt()])
		}

		for (x in -12..12 step 6) for (z in -12..12 step 6) setBlock(vec3(x, FLOOR_Y + 4, z), Blocks.LIGHT(mapOf("level" to "15")))

		listOf(-112.5, -67.5, -22.5, 22.5, 67.5, 112.5).map { polar(13.4, it) }.forEach { (x, z) ->
			fill(vec3(x, FLOOR_Y + 1, z), vec3(x, FLOOR_Y + 2, z), Blocks.POLISHED_BLACKSTONE_WALL)
			setBlock(vec3(x, FLOOR_Y + 3, z), Blocks.LANTERN)
		}

		Tier.entries.forEach { setBlock(vec3(it.x, FLOOR_Y + 1, it.z), it.pedestal) }
		setBlock(vec3(FORGE.first, FLOOR_Y + 1, FORGE.second), Blocks.ANVIL)
		setBlock(vec3(ALTAR.first, FLOOR_Y + 1, ALTAR.second), Blocks.ENCHANTING_TABLE)
	}

	private val entities = dataPack.function("entities", directory = "factory") {
		val core = vec3(center(0), CORE_Y, center(0))
		summonItem(Items.STONE, core, 3f, "ot.core", "ot.spin_slow")
		summonInteraction(vec3(center(0), CORE_Y - 1.65, center(0)), 3.3f, 3.3f, "ot.use_core")
		summonText(
			textComponent("ORE TYCOON", Color.GOLD) { bold = true },
			vec3(center(0), CORE_Y + 5.2, center(0)), 3f, "ot.title",
		) { this["background"] = 0 }
		summonText(textComponent(""), vec3(center(0), CORE_Y + 2.2, center(0)), 1.4f, "ot.core_label")
		summonText(
			textComponent("Punch the core to mine coins", Color.YELLOW) +
				text("${NEWLINE}Buy drills to mine for you", Color.GRAY),
			vec3(center(0), FLOOR_Y + 1.1, 10.5), 0.6f, "ot.hint",
		)

		Tier.entries.forEach { tier ->
			summonItem(Items.GLASS, vec3(center(tier.x), FLOOR_Y + 2.9, center(tier.z)), 0.8f, "ot.machine", "ot.machine.${tier.id}", "ot.spin")
			summonText(textComponent(""), vec3(center(tier.x), FLOOR_Y + 3.6, center(tier.z)), 0.7f, "ot.label", "ot.label.${tier.id}")
			summonInteraction(vec3(center(tier.x), FLOOR_Y + 1.0, center(tier.z)), 1.5f, 2.6f, "ot.buy.${tier.id}")
		}

		val (forgeX, forgeZ) = FORGE
		summonItem(Pickaxe.STONE.item, vec3(center(forgeX), FLOOR_Y + 2.6, center(forgeZ)), 0.9f, "ot.forge_item", "ot.spin")
		summonText(textComponent(""), vec3(center(forgeX), FLOOR_Y + 3.3, center(forgeZ)), 0.7f, "ot.forge_label")
		summonInteraction(vec3(center(forgeX), FLOOR_Y + 1.0, center(forgeZ)), 1.4f, 2.2f, "ot.buy.pickaxe")

		val (altarX, altarZ) = ALTAR
		summonItem(Items.NETHER_STAR, vec3(center(altarX), FLOOR_Y + 2.5, center(altarZ)), 0.8f, "ot.altar_item", "ot.spin")
		summonText(textComponent(""), vec3(center(altarX), FLOOR_Y + 3.3, center(altarZ)), 0.7f, "ot.altar_label")
		summonInteraction(vec3(center(altarX), FLOOR_Y + 1.0, center(altarZ)), 1.4f, 2.2f, "ot.prestige")
	}

	/** Puts every visual back in line with the scores: machines, core block, forge pickaxe and the victory beacon. */
	val sync = dataPack.function("sync", directory = "factory") {
		Tier.entries.forEach { tier ->
			execute {
				ifCondition { owned.getValue(tier).value greaterThanOrEqualTo 1 }
				run { animate(Entities.machine(tier), 1f, 6, item = tier.machine) }
			}
			execute {
				ifCondition { owned.getValue(tier).value equalTo 0 }
				run { animate(Entities.machine(tier), 0.8f, 6, item = Items.GLASS) }
			}
		}
		Level.entries.forEach { stage ->
			execute {
				ifCondition { level.value equalTo stage.number }
				run { data(Entities.core).modify("item", itemNbt(stage.core)) }
			}
		}
		Pickaxe.entries.forEach { pick ->
			execute {
				ifCondition { pickaxe.value equalTo pick.ordinal }
				run { animate(Entities.forgeItem, 0.9f, 6, item = pick.next?.item ?: pick.item) }
			}
		}
		execute {
			ifCondition { level.value equalTo Level.FINAL.number }
			run { setBlock(vec3(0, FLOOR_Y, 0), Blocks.BEACON) }
		}
		execute {
			unlessCondition { level.value equalTo Level.FINAL.number }
			run { setBlock(vec3(0, FLOOR_Y, 0), Blocks.GOLD_BLOCK) }
		}
	}

	/**
	 * Builds the whole factory once the area is loaded, which takes a few ticks after the first player arrives.
	 * `execute if loaded` checks one chunk per call, so the four corners are tested.
	 */
	val build = dataPack.function("build", directory = "factory") {
		listOf(-REACH to -REACH, -REACH to REACH, REACH to -REACH, REACH to REACH).forEach { (x, z) ->
			execute {
				unlessCondition { loaded(vec3(x, FLOOR_Y, z)) }
				run { returnRun { schedule("$NAMESPACE:factory/build").replace(10.ticks) } }
			}
		}
		function(clear)
		function(blocks)
		function(entities)
		built.set(1)
		function(sync)
	}
}
