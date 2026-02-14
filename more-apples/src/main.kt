import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.components.matchers.enchantments
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.types.resources.RandomSequenceArgument
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.explosionDecay
import io.github.ayfri.kore.features.itemmodifiers.functions.setCount
import io.github.ayfri.kore.features.loottables.*
import io.github.ayfri.kore.features.loottables.entries.*
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.*
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.features.predicates.sub.predicates
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack
import kotlinx.io.files.Path

private fun Predicate.withSilkTouchShears() = anyOf {
	matchTool(Items.SHEARS)
	matchTool {
		predicates {
			enchantments(Enchantments.SILK_TOUCH, level = rangeOrIntStart(1))
		}
	}
}

val isGitHubCI get() = System.getenv("CI") != null

fun main() {
	dataPack("more-apples") {
		configuration {
			prettyPrint = true
		}

		iconPath = Path("more-apples/pack.png")

		pack {
			supportedFormats(min = 61)
			description = textComponent("More Apples recreated using Kore, inspired by Stoupy51 MoreApples")
		}

		val leaves = listOf(
			Items.ACACIA_LEAVES to Items.ACACIA_SAPLING,
			Items.AZALEA_LEAVES to Items.AZALEA,
			Items.BIRCH_LEAVES to Items.BIRCH_SAPLING,
			Items.CHERRY_LEAVES to Items.CHERRY_SAPLING,
			Items.DARK_OAK_LEAVES to Items.DARK_OAK_SAPLING,
			Items.FLOWERING_AZALEA_LEAVES to Items.FLOWERING_AZALEA,
			Items.JUNGLE_LEAVES to Items.JUNGLE_SAPLING,
			Items.MANGROVE_LEAVES to Items.MANGROVE_PROPAGULE,
			Items.OAK_LEAVES to Items.OAK_SAPLING,
			Items.PALE_OAK_LEAVES to Items.PALE_OAK_SAPLING,
			Items.SPRUCE_LEAVES to Items.SPRUCE_SAPLING,
		)

		leaves.forEach { (leaf, sapling) ->
			val leafId = leaf.asString().removePrefix("minecraft:")
			lootTable("blocks/$leafId") {
				namespace = "minecraft"
				type = LootTableType.BLOCK

				pool {
					bonusRolls = constant(0f)
					entries {
						alternatives {
							children {
								item(leaf) {
									conditions {
										withSilkTouchShears()
									}
								}

								item(sapling) {
									conditions {
										survivesExplosion()
										tableBonus(Enchantments.FORTUNE, 0.05f, 0.0625f, 0.083333f, 0.1f)
									}
								}
							}
						}
					}
				}
				pool {
					bonusRolls = constant(0f)
					entries {
						item(Items.STICK) {
							conditions {
								tableBonus(Enchantments.FORTUNE, 0.02f, 0.022222f, 0.025f, 0.033333f, 0.1f)
							}
							functions {
								setCount(uniform(1f, 2f), false)
								explosionDecay()
							}
						}
					}

					conditions {
						inverted {
							withSilkTouchShears()
						}
					}
				}

				pool(constant(2f)) {
					bonusRolls = constant(0f)
					entries {
						item(Items.APPLE) {
							conditions {
								survivesExplosion()
								tableBonus(Enchantments.FORTUNE, 0.005f, 0.005555f, 0.00625f, 0.008333f, 0.025f)
							}
						}
					}

					conditions {
						inverted {
							withSilkTouchShears()
						}
					}
				}
				randomSequence = RandomSequenceArgument("blocks/$leafId")
			}
		}

		when {
			isGitHubCI -> generateZip()
			else -> generate()
		}
	}
}
