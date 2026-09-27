tag @s add ot.in
team join ot.players @s
effect give @s minecraft:saturation infinite 0 true
title @s title {type:"text",bold:1b,color:"gold",text:"ORE TYCOON"}
title @s subtitle {type:"text",color:"yellow",text:"Punch the core to mine coins"}
playsound minecraft:block.beacon.activate master @s ~ ~ ~ 1.4
function ore_tycoon:hud/refresh