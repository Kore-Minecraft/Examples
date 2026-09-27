scoreboard players set #per_second ot.data 0
scoreboard players operation #temp ot.data = #owned.coal ot.data
scoreboard players operation #temp ot.data *= #1 ot.data
scoreboard players operation #per_second ot.data += #temp ot.data
scoreboard players operation #temp ot.data = #owned.copper ot.data
scoreboard players operation #temp ot.data *= #6 ot.data
scoreboard players operation #per_second ot.data += #temp ot.data
scoreboard players operation #temp ot.data = #owned.iron ot.data
scoreboard players operation #temp ot.data *= #40 ot.data
scoreboard players operation #per_second ot.data += #temp ot.data
scoreboard players operation #temp ot.data = #owned.gold ot.data
scoreboard players operation #temp ot.data *= #220 ot.data
scoreboard players operation #per_second ot.data += #temp ot.data
scoreboard players operation #temp ot.data = #owned.diamond ot.data
scoreboard players operation #temp ot.data *= #1300 ot.data
scoreboard players operation #per_second ot.data += #temp ot.data
scoreboard players operation #temp ot.data = #owned.emerald ot.data
scoreboard players operation #temp ot.data *= #8000 ot.data
scoreboard players operation #per_second ot.data += #temp ot.data
scoreboard players operation #temp ot.data = #owned.netherite ot.data
scoreboard players operation #temp ot.data *= #50000 ot.data
scoreboard players operation #per_second ot.data += #temp ot.data
scoreboard players operation #multiplier ot.data = #stars ot.data
scoreboard players operation #multiplier ot.data *= #25 ot.data
scoreboard players add #multiplier ot.data 100
scoreboard players operation #per_second ot.data *= #multiplier ot.data
scoreboard players operation #per_second ot.data /= #100 ot.data
execute if score #pickaxe ot.data matches 0 run scoreboard players set #pickaxe.flat ot.data 1
execute if score #pickaxe ot.data matches 0 run scoreboard players set #pickaxe.percent ot.data 0
execute if score #pickaxe ot.data matches 1 run scoreboard players set #pickaxe.flat ot.data 3
execute if score #pickaxe ot.data matches 1 run scoreboard players set #pickaxe.percent ot.data 1
execute if score #pickaxe ot.data matches 2 run scoreboard players set #pickaxe.flat ot.data 10
execute if score #pickaxe ot.data matches 2 run scoreboard players set #pickaxe.percent ot.data 2
execute if score #pickaxe ot.data matches 3 run scoreboard players set #pickaxe.flat ot.data 40
execute if score #pickaxe ot.data matches 3 run scoreboard players set #pickaxe.percent ot.data 4
execute if score #pickaxe ot.data matches 4 run scoreboard players set #pickaxe.flat ot.data 200
execute if score #pickaxe ot.data matches 4 run scoreboard players set #pickaxe.percent ot.data 8
scoreboard players operation #per_click ot.data = #pickaxe.flat ot.data
scoreboard players operation #per_click ot.data *= #multiplier ot.data
scoreboard players operation #per_click ot.data /= #100 ot.data
scoreboard players operation #temp ot.data = #per_second ot.data
scoreboard players operation #temp ot.data *= #pickaxe.percent ot.data
scoreboard players operation #temp ot.data /= #100 ot.data
scoreboard players operation #per_click ot.data += #temp ot.data
scoreboard players operation #format.input ot.data = #per_second ot.data
function ore_tycoon:format/number
data modify storage ore_tycoon:data hud.per_second set from storage ore_tycoon:data hud.out
scoreboard players operation #format.input ot.data = #per_click ot.data
function ore_tycoon:format/number
data modify storage ore_tycoon:data hud.per_click set from storage ore_tycoon:data hud.out