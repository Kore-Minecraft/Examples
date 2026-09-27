execute if score #owned.gold ot.data matches 25.. run return run title @s actionbar {type:"text",color:"gold",text:"Gold Drill is maxed out!"}
scoreboard players operation #temp ot.data = #price.gold ot.data
scoreboard players operation #temp ot.data -= #coins ot.data
execute if score #temp ot.data matches 1.. run return run function ore_tycoon:shop/too_poor
scoreboard players operation #coins ot.data -= #price.gold ot.data
scoreboard players add #owned.gold ot.data 1
function ore_tycoon:economy/prices
function ore_tycoon:economy/recalculate
data merge entity @e[limit=1,tag=ot.machine.gold,type=minecraft:item_display] {item:{id:"minecraft:gold_ore",count:1},interpolation_duration:4,start_interpolation:0,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.5f,1.5f,1.5f],translation:[0.0f,0.0f,0.0f]}}
schedule function ore_tycoon:factory/sync 5 replace
execute positioned 0.5 281.9 -8.5 run function ore_tycoon:generated_scopes/vfx_purchase_ring
particle dust{color:16767037,scale:1.4d} 0.5 281.9 -8.5 0.4 0.4 0.4 0 24
playsound minecraft:block.anvil.use master @s ~ ~ ~ 0.5 1.6
playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.8
title @s actionbar [{type:"text",color:"#ffd83d",text:"Gold Drill bought! "},{type:"text",color:"gray",text:"You own "},{type:"score",color:"white",score:{name:"#owned.gold",objective:"ot.data"}}]
function ore_tycoon:hud/labels