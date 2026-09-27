import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.particle.particles
import io.github.ayfri.kore.commands.particle.types.dust
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.commands.returnIf
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.helpers.vfx.drawCircle
import io.github.ayfri.kore.scoreboard.*

class Economy(dataPack: DataPack, formatter: NumberFormatter) {
	private val flat = global("pickaxe.flat")
	private val percent = global("pickaxe.percent")

	/** Adds [gain] to the coins and the lifetime total. Gains are capped at [MAX_GAIN] so the sums can never overflow. */
	val earn = dataPack.function("earn", directory = "economy") {
		gain minWith Constants[MAX_GAIN]
		coins += gain
		coins minWith Constants[MAX_COINS]
		lifetime += gain
		lifetime minWith Constants[MAX_COINS]
	}

	/** Production per second and coins per click, from the drills, the pickaxe and the prestige stars. */
	val recalculate = dataPack.function("recalculate", directory = "economy") {
		perSecond.set(0)
		Tier.entries.forEach { tier ->
			temp setTo owned.getValue(tier)
			temp *= Constants[tier.production]
			perSecond += temp
		}
		multiplier setTo stars
		multiplier *= Constants[STAR_BONUS_PERCENT]
		multiplier += 100
		perSecond *= multiplier
		perSecond /= Constants[100]

		Pickaxe.entries.forEach { pick ->
			execute {
				ifCondition { pickaxe.value equalTo pick.ordinal }
				run { flat.set(pick.flat) }
			}
			execute {
				ifCondition { pickaxe.value equalTo pick.ordinal }
				run { percent.set(pick.percent) }
			}
		}
		perClick setTo flat
		perClick *= multiplier
		perClick /= Constants[100]
		temp setTo perSecond
		temp *= percent
		temp /= Constants[100]
		perClick += temp

		formatter.format(perSecond, "per_second")
		formatter.format(perClick, "per_click")
	}

	/** Reads the next prices from the tables [Tier.prices] computed in Kotlin: no float math at runtime. */
	val prices = dataPack.function("prices", directory = "economy") {
		Tier.entries.forEach { tier ->
			val next = price.getValue(tier)
			tier.prices.forEachIndexed { count, value ->
				execute {
					ifCondition { owned.getValue(tier).value equalTo count }
					run { next.set(value) }
				}
			}
			execute {
				ifCondition { owned.getValue(tier).value greaterThanOrEqualTo MAX_OWNED }
				run { next.set(Int.MAX_VALUE) }
			}
			formatter.format(next, "price.${tier.id}")
		}
		Pickaxe.entries.forEach { pick ->
			execute {
				ifCondition { pickaxe.value equalTo pick.ordinal }
				run { pickaxePrice.set(pick.next?.cost ?: Int.MAX_VALUE) }
			}
		}
		formatter.format(pickaxePrice, "price.pickaxe")
	}
}

class Shop(dataPack: DataPack, economy: Economy, formatter: NumberFormatter, factory: Factory, hud: Hud) {
	private val ring = dataPack.drawCircle("purchase_ring", Particles.END_ROD, radius = 1.2, points = 16)

	/** Tells the buyer how much they miss, the difference being in [temp]. */
	private val tooPoor = dataPack.function("too_poor", directory = "shop") {
		formatter.format(temp, "missing")
		actionBar(textComponent("Not enough coins, ", Color.RED) + coins("missing") + text(" missing", Color.RED))
		playSound(SoundEvents.Entity.Villager.NO, PlaySoundMixer.MASTER, self(), vec3(), volume = 0.6)
	}

	/** Runs as the buyer: pay, refresh the prices and production, then pop the machine and burst particles. */
	val buy = Tier.entries.associateWith { tier ->
		dataPack.function("buy_${tier.id}", directory = "shop") {
			val count = owned.getValue(tier)
			val cost = price.getValue(tier)
			val machine = vec3(center(tier.x), FLOOR_Y + 2.9, center(tier.z))

			returnIf({ count.value greaterThanOrEqualTo MAX_OWNED }) {
				actionBar(textComponent("${tier.title} is maxed out!", Color.GOLD))
			}
			temp setTo cost
			temp -= coins
			returnIf({ temp.value greaterThan 0 }) { function(tooPoor) }

			coins -= cost
			count += 1
			function(economy.prices)
			function(economy.recalculate)

			animate(Entities.machine(tier), 1.5f, 4, item = tier.machine)
			schedule(factory.sync).replace(5.ticks)
			execute {
				positioned(machine)
				run { function(ring) }
			}
			particles { dust(tier.color, 1.4, machine, vec3(0.4, 0.4, 0.4), 0.0, 24) }
			playSound(SoundEvents.Block.Anvil.USE, PlaySoundMixer.MASTER, self(), vec3(), volume = 0.5, pitch = 1.6)
			playSound(SoundEvents.Entity.ExperienceOrb.PICKUP, PlaySoundMixer.MASTER, self(), vec3(), volume = 0.8)
			actionBar(textComponent("${tier.title} bought! ", tier.color) + text("You own ", Color.GRAY) + count.text(Color.WHITE))
			function(hud.labels)
		}
	}

	val upgradePickaxe = dataPack.function("pickaxe", directory = "shop") {
		val best = Pickaxe.entries.last()
		returnIf({ pickaxe.value greaterThanOrEqualTo best.ordinal }) {
			actionBar(textComponent("You already own the ${best.title}!", Color.GOLD))
		}
		temp setTo pickaxePrice
		temp -= coins
		returnIf({ temp.value greaterThan 0 }) { function(tooPoor) }

		coins -= pickaxePrice
		pickaxe += 1
		function(economy.prices)
		function(economy.recalculate)

		animate(Entities.forgeItem, 1.5f, 4)
		schedule(factory.sync).replace(5.ticks)
		val (forgeX, forgeZ) = FORGE
		execute {
			positioned(vec3(center(forgeX), FLOOR_Y + 2.6, center(forgeZ)))
			run { function(ring) }
		}
		playSound(SoundEvents.Block.SmithingTable.USE, PlaySoundMixer.MASTER, self())
		playSound(SoundEvents.Block.Anvil.USE, PlaySoundMixer.MASTER, self(), vec3(), volume = 0.5, pitch = 1.2)
		Pickaxe.entries.drop(1).forEach { pick ->
			execute {
				ifCondition { pickaxe.value equalTo pick.ordinal }
				run { actionBar(textComponent("Upgraded to the ${pick.title}!", Color.AQUA)) }
			}
		}
		function(hud.labels)
	}
}
