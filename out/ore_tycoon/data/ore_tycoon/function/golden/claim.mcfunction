execute at @e[tag=ot.golden,type=minecraft:item_display] run particle minecraft:totem_of_undying ~ ~ ~ 0.3 0.3 0.3 0.5 40
kill @e[tag=ot.golden]
scoreboard players set #golden_life ot.data 0
execute store result score #golden_timer ot.data run random value 45..90
playsound minecraft:entity.player.levelup master @s ~ ~ ~ 1.5
playsound minecraft:block.amethyst_cluster.break master @s
execute store result score #random ot.data run random value 0..2
execute if score #random ot.data matches 2 run return run function ore_tycoon:golden/frenzy
scoreboard players operation #gain ot.data = #per_second ot.data
scoreboard players operation #gain ot.data *= #30 ot.data
scoreboard players operation #gain ot.data > #100 ot.data
function ore_tycoon:economy/earn
scoreboard players operation #format.input ot.data = #gain ot.data
function ore_tycoon:format/number
data modify storage ore_tycoon:data hud.lucky set from storage ore_tycoon:data hud.out
title @s title {type:"text",bold:1b,color:"gold",text:"LUCKY!"}
title @s subtitle [{type:"text",color:"yellow",text:"+"},{type:"nbt",color:"yellow",interpret:1b,nbt:"hud.lucky",source:"storage",storage:"ore_tycoon:data"},{type:"text",color:"gold",text:" ⛃"}]
tellraw @a[tag=ot.in] [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"selector",selector:"@s"},{type:"text",color:"yellow",text:" found a Golden Nugget worth "},{type:"nbt",color:"gold",interpret:1b,nbt:"hud.lucky",source:"storage",storage:"ore_tycoon:data"},{type:"text",color:"gold",text:" ⛃"}]