scoreboard players set #frenzy ot.data 20
title @s title {type:"text",bold:1b,color:"light_purple",text:"FRENZY!"}
title @s subtitle {type:"text",color:"yellow",text:"×3 coins for 20 seconds"}
tellraw @a[tag=ot.in] [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"selector",selector:"@s"},{type:"text",color:"yellow",text:" started a "},{type:"text",bold:1b,color:"light_purple",text:"×3 FRENZY"}]