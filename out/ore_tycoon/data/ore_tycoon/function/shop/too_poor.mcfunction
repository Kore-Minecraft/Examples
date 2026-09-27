scoreboard players operation #format.input ot.data = #temp ot.data
function ore_tycoon:format/number
data modify storage ore_tycoon:data hud.missing set from storage ore_tycoon:data hud.out
title @s actionbar [{type:"text",color:"red",text:"Not enough coins, "},{type:"nbt",color:"gold",interpret:1b,nbt:"hud.missing",source:"storage",storage:"ore_tycoon:data"},{type:"text",color:"gold",text:" ⛃"},{type:"text",color:"red",text:" missing"}]
playsound minecraft:entity.villager.no master @s ~ ~ ~ 0.6