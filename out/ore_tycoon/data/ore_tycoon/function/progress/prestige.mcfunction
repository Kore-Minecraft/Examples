execute if score #level ot.data matches ..7 run return run title @s actionbar {type:"text",color:"red",text:"Mine 1B coins in this run to unlock the prestige"}
execute if score #stars ot.data matches 20.. run return run title @s actionbar {type:"text",color:"gold",text:"You reached the maximum of 20 stars!"}
scoreboard players add #stars ot.data 1
function ore_tycoon:progress/reset_run
particle minecraft:totem_of_undying 4.5 281.5 11.5 0.5 1.0 0.5 0.4 80
playsound minecraft:block.beacon.power_select master @a[tag=ot.in]
title @a[tag=ot.in] title {type:"text",bold:1b,color:"light_purple",text:"★ PRESTIGE ★"}
title @a[tag=ot.in] subtitle {type:"text",color:"yellow",text:"+25% production and clicks, forever"}
tellraw @a[tag=ot.in] [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"selector",selector:"@s"},{type:"text",color:"light_purple",text:" prestiged! Stars: "},{type:"score",color:"white",score:{name:"#stars",objective:"ot.data"}}]