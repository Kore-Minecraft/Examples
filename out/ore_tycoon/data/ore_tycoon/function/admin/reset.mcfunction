scoreboard players set #stars ot.data 0
function ore_tycoon:progress/reset_run
tellraw @a [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"text",color:"red",text:"Progress reset, stars included."}]