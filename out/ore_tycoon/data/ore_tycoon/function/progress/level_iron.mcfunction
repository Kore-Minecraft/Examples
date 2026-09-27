scoreboard players set #level ot.data 3
function ore_tycoon:factory/sync
data merge entity @e[limit=1,tag=ot.core,type=minecraft:item_display] {interpolation_duration:5,start_interpolation:0,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[3.8f,3.8f,3.8f],translation:[0.0f,0.0f,0.0f]}}
scoreboard players set #squish ot.data 7
execute positioned 0.5 280.0 0.5 run function ore_tycoon:generated_scopes/vfx_level_helix
title @a[tag=ot.in] title {type:"text",bold:1b,color:"gold",text:"LEVEL 3"}
title @a[tag=ot.in] subtitle {type:"text",color:"yellow",text:"The core turned into Iron Ore"}
playsound minecraft:ui.toast.challenge_complete master @a[tag=ot.in]
tellraw @a[tag=ot.in] [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"text",color:"green",text:"Level 3 reached, "},{type:"text",color:"yellow",text:"the core turned into Iron Ore!"}]