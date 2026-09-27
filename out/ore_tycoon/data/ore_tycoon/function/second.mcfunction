scoreboard players operation #gain ot.data = #per_second ot.data
execute if score #frenzy ot.data matches 1.. run scoreboard players operation #gain ot.data *= #3 ot.data
function ore_tycoon:economy/earn
execute if score #frenzy ot.data matches 1.. run scoreboard players remove #frenzy ot.data 1
function ore_tycoon:progress/check_level
function ore_tycoon:golden/second
function ore_tycoon:player/second
execute as @a[tag=!ot.welcomed] run function ore_tycoon:player/welcome
execute unless entity @a[tag=ot.in] run return 0
execute as @e[tag=ot.spin,type=minecraft:item_display] at @s run teleport @s ~ ~ ~ ~90 ~
execute as @e[tag=ot.spin_slow,type=minecraft:item_display] at @s run teleport @s ~ ~ ~ ~30 ~
function ore_tycoon:flyers/launch
function ore_tycoon:hud/refresh