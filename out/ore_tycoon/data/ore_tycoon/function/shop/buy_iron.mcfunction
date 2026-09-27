execute if score #owned.iron ot.data matches 25.. run return run title @s actionbar {type:"text",color:"gold",text:"Iron Drill is maxed out!"}
scoreboard players operation #temp ot.data = #price.iron ot.data
scoreboard players operation #temp ot.data -= #coins ot.data
execute if score #temp ot.data matches 1.. run return run function ore_tycoon:shop/too_poor
scoreboard players operation #coins ot.data -= #price.iron ot.data
scoreboard players add #owned.iron ot.data 1
function ore_tycoon:economy/prices
function ore_tycoon:economy/recalculate
data merge entity @e[limit=1,tag=ot.machine.iron,type=minecraft:item_display] {item:{id:"minecraft:iron_ore",count:1},interpolation_duration:4,start_interpolation:0,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.5f,1.5f,1.5f],translation:[0.0f,0.0f,0.0f]}}
schedule function ore_tycoon:factory/sync 5 replace
execute positioned -5.5 281.9 -5.5 run function ore_tycoon:generated_scopes/vfx_purchase_ring
particle dust{color:15258303,scale:1.4d} -5.5 281.9 -5.5 0.4 0.4 0.4 0 24
playsound minecraft:block.anvil.use master @s ~ ~ ~ 0.5 1.6
playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.8
title @s actionbar [{type:"text",color:"#e8d2bf",text:"Iron Drill bought! "},{type:"text",color:"gray",text:"You own "},{type:"score",color:"white",score:{name:"#owned.iron",objective:"ot.data"}}]
function ore_tycoon:hud/labels