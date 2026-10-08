execute unless score @s ot.pid matches 1.. run scoreboard players add #next_player_id ot.data 1
execute unless score @s ot.pid matches 1.. run scoreboard players operation @s ot.pid = #next_player_id ot.data
execute if entity @s[tag=ot.waiting] run return run title @s actionbar {type:"text",color:"yellow",text:"The factory is being built..."}
execute unless entity @s[tag=ot.in] run function ore_tycoon:player/remember
execute if score #built ot.data matches 1 run return run function ore_tycoon:player/enter
tag @s add ot.waiting
function ore_tycoon:factory/request_build
title @s actionbar {type:"text",color:"yellow",text:"Building the factory..."}