$scoreboard players set #gain ot.data $(amount)
function ore_tycoon:economy/earn
function ore_tycoon:hud/refresh
tellraw @s [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"text",color:"green",text:"Added "},{type:"score",color:"gold",score:{name:"#gain",objective:"ot.data"}},{type:"text",color:"green",text:" coins."}]