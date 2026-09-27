execute if score #squish ot.data matches 1 run function ore_tycoon:core/release
execute if score #squish ot.data matches 1.. run scoreboard players remove #squish ot.data 1
execute as @e[tag=ot.pop_new,type=minecraft:text_display] run data merge entity @s {interpolation_duration:12,start_interpolation:0,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[0.9f,0.9f,0.9f],translation:[0.0f,1.2f,0.0f]}}
tag @e[tag=ot.pop_new,type=minecraft:text_display] remove ot.pop_new
scoreboard players add @e[tag=ot.pop,type=minecraft:text_display] ot.age 1
kill @e[scores={ot.age=13..},tag=ot.pop,type=minecraft:text_display]
execute if score #clock ot.data matches 1 run function ore_tycoon:flyers/rise
execute if score #clock ot.data matches 10 run function ore_tycoon:flyers/dive
execute if score #clock ot.data matches 19 run function ore_tycoon:flyers/land
execute at @e[tag=ot.golden,type=minecraft:item_display] run particle minecraft:wax_on ~ ~ ~ 0.35 0.35 0.35 0 1