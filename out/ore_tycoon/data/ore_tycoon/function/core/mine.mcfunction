scoreboard players operation #gain ot.data = #per_click ot.data
execute if score #frenzy ot.data matches 1.. run scoreboard players operation #gain ot.data *= #3 ot.data
function ore_tycoon:economy/earn
scoreboard players operation #format.input ot.data = #gain ot.data
function ore_tycoon:format/number
data modify storage ore_tycoon:data hud.gain set from storage ore_tycoon:data hud.out
execute anchored eyes positioned ^ ^ ^1.8 run function ore_tycoon:core/burst
data merge entity @e[limit=1,tag=ot.core,type=minecraft:item_display] {interpolation_duration:2,start_interpolation:0,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[2.7f,2.7f,2.7f],translation:[0.0f,0.0f,0.0f]}}
scoreboard players set #squish ot.data 3
playsound minecraft:block.stone.break master @s ~ ~ ~ 0.5 1.3
function ore_tycoon:hud/core_label