function ore_tycoon:factory/clear
execute in minecraft:overworld run forceload remove -20 -20 19 19
scoreboard objectives remove ot.data
scoreboard objectives remove ot.age
scoreboard objectives remove ot.pid
scoreboard objectives remove ot.sidebar
scoreboard objectives remove ore_tycoon.menu.main
bossbar remove ore_tycoon:progress
team remove ot.players
data remove storage ore_tycoon:data args
data remove storage ore_tycoon:data hud
data remove storage ore_tycoon:data players
tellraw @a [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"text",color:"yellow",text:"The factory and its data are gone. Remove the datapack file, or it comes back on the next /reload."}]