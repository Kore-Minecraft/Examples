execute if score #pickaxe ot.data matches 4.. run return run title @s actionbar {type:"text",color:"gold",text:"You already own the Netherite Pickaxe!"}
scoreboard players operation #temp ot.data = #price.pickaxe ot.data
scoreboard players operation #temp ot.data -= #coins ot.data
execute if score #temp ot.data matches 1.. run return run function ore_tycoon:shop/too_poor
scoreboard players operation #coins ot.data -= #price.pickaxe ot.data
scoreboard players add #pickaxe ot.data 1
function ore_tycoon:economy/prices
function ore_tycoon:economy/recalculate
data merge entity @e[limit=1,tag=ot.forge_item,type=minecraft:item_display] {interpolation_duration:4,start_interpolation:0,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.5f,1.5f,1.5f],translation:[0.0f,0.0f,0.0f]}}
schedule function ore_tycoon:factory/sync 5 replace
execute positioned -3.5 281.6 11.5 run function ore_tycoon:generated_scopes/vfx_purchase_ring
playsound minecraft:block.smithing_table.use master @s
playsound minecraft:block.anvil.use master @s ~ ~ ~ 0.5 1.2
execute if score #pickaxe ot.data matches 1 run title @s actionbar {type:"text",color:"aqua",text:"Upgraded to the Stone Pickaxe!"}
execute if score #pickaxe ot.data matches 2 run title @s actionbar {type:"text",color:"aqua",text:"Upgraded to the Iron Pickaxe!"}
execute if score #pickaxe ot.data matches 3 run title @s actionbar {type:"text",color:"aqua",text:"Upgraded to the Diamond Pickaxe!"}
execute if score #pickaxe ot.data matches 4 run title @s actionbar {type:"text",color:"aqua",text:"Upgraded to the Netherite Pickaxe!"}
function ore_tycoon:hud/labels