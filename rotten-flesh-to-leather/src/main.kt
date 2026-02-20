import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.recipes.recipes
import io.github.ayfri.kore.features.recipes.types.ingredient
import io.github.ayfri.kore.features.recipes.types.result
import io.github.ayfri.kore.features.recipes.types.smelting
import io.github.ayfri.kore.features.recipes.types.smoking
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.pack.packFormat
import kotlinx.io.files.Path

val isGitHubCI get() = System.getenv("CI") != null

fun main() {
	dataPack("rotten-flesh-to-leather") {
		configuration {
			prettyPrint = !isGitHubCI
		}

		iconPath = Path("rotten-flesh-to-leather/pack.png")

		pack {
			minFormat = packFormat(81)
			description = textComponent("Smoke flesh for leather, and smelt it for brown dye!")
		}

		recipes {
			smelting("browndye_smelt") {
				ingredient(Items.ROTTEN_FLESH)
				result(Items.BROWN_DYE)
				experience = 0.1
				cookingTime = 200
			}

			smoking("leather_smoke") {
				ingredient(Items.ROTTEN_FLESH)
				result(Items.LEATHER)
				experience = 0.1
				cookingTime = 100
			}
		}

		when {
			isGitHubCI -> generateZip()
			else -> generate()
		}
	}
}
