execute unless score @s ot.pid matches 1.. run scoreboard players add #next_player_id ot.data 1
execute unless score @s ot.pid matches 1.. run scoreboard players operation @s ot.pid = #next_player_id ot.data
execute unless entity @s[tag=ot.in] run execute store result storage ore_tycoon:data args.id int 1 run scoreboard players get @s ot.pid
execute unless entity @s[tag=ot.in] run function ore_tycoon:player/save_return with storage ore_tycoon:data args
execute if entity @s[gamemode=survival] run tag @s add ot.was_survival
execute if entity @s[tag=ot.was_survival] run gamemode adventure @s
execute in minecraft:overworld run teleport @s 0.5 280.0 13.5 180 0
execute unless score #built ot.data matches 1 run schedule function ore_tycoon:factory/build 5 replace
schedule function ore_tycoon:factory/sync 20 replace
function ore_tycoon:player/join