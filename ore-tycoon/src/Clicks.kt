import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.advancement
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.Relation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.rewards
import io.github.ayfri.kore.features.advancements.triggers.playerHurtEntity
import io.github.ayfri.kore.features.advancements.triggers.playerInteractedWithEntity
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument

/**
 * The criteria have no entity condition: 26.2 reads it as an entity predicate, 26.3 as a loot condition and refuses to
 * load the world otherwise. The reward function filters the clicked entity itself.
 */
private enum class Click(val field: String, val relation: Relation, val criterion: AdvancementCriteria.() -> Unit) {
	USE("interaction", Relation.TARGET, { playerInteractedWithEntity("click") }),
	ATTACK("attack", Relation.ATTACKER, { playerHurtEntity("click") });

	val id = "click_${name.lowercase()}"
}

/**
 * Detects right and left clicks on the interaction boxes. The game grants an advancement, its reward function finds
 * the box holding click data, and the box's scoreboard tag picks the [actions] entry, run as and at the clicking player.
 */
fun DataPack.clickDetection(actions: Map<String, FunctionArgument>) = Click.entries.forEach { click ->
	val onBox = function("${click.name.lowercase()}_box", directory = "click") {
		actions.forEach { (tag, action) ->
			execute {
				ifCondition { entity(self { this.tag = tag }) }
				on(click.relation)
				at(self())
				run(action)
			}
		}
		data(self()).remove(click.field)
	}

	val reward = function(click.name.lowercase(), directory = "click") {
		advancement { revoke(self(), AdvancementArgument(click.id, NAMESPACE)) }
		execute {
			asTarget(Entities.clickable)
			ifCondition { data(self(), click.field) }
			run(onBox)
		}
	}

	advancement(click.id) {
		criteria(click.criterion)
		rewards { function = reward }
	}
}
