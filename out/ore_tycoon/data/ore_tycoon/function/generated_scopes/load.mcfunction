scoreboard objectives add ot.data dummy
scoreboard objectives add ot.age dummy
scoreboard objectives add ot.pid dummy
scoreboard players set #0 ot.data 0
scoreboard players set #1 ot.data 1
scoreboard players set #3 ot.data 3
scoreboard players set #6 ot.data 6
scoreboard players set #10 ot.data 10
scoreboard players set #25 ot.data 25
scoreboard players set #30 ot.data 30
scoreboard players set #40 ot.data 40
scoreboard players set #100 ot.data 100
scoreboard players set #220 ot.data 220
scoreboard players set #1000 ot.data 1000
scoreboard players set #1300 ot.data 1300
scoreboard players set #8000 ot.data 8000
scoreboard players set #10000 ot.data 10000
scoreboard players set #50000 ot.data 50000
scoreboard players set #100000 ot.data 100000
scoreboard players set #1000000 ot.data 1000000
scoreboard players set #10000000 ot.data 10000000
scoreboard players set #100000000 ot.data 100000000
scoreboard players set #1000000000 ot.data 1000000000
scoreboard players set #2000000000 ot.data 2000000000
team add ot.players {"type":"text","color":"gold","text":"Ore Tycoon"}
team modify ot.players color gold
team modify ot.players collisionRule never
team modify ot.players friendlyFire false
bossbar add ore_tycoon:progress {"type":"text","color":"gold","text":"Ore Tycoon"}
bossbar set ore_tycoon:progress style notched_10
execute unless score #initialized ot.data matches 1 run function ore_tycoon:install
function ore_tycoon:economy/prices
function ore_tycoon:economy/recalculate
scoreboard objectives remove ot.sidebar
scoreboard objectives add ot.sidebar dummy {type:"text",bold:1b,color:"gold",text:"⛏ ORE TYCOON ⛏"}
scoreboard objectives modify ot.sidebar numberformat blank
scoreboard players set $0 ot.sidebar 0
scoreboard players display name $0 ot.sidebar ""
scoreboard players set $1 ot.sidebar -1
scoreboard players display name $1 ot.sidebar {type:"text",color:"gray",text:"Coins"}
scoreboard players display numberformat $1 ot.sidebar fixed {type:"nbt",color:"gold",interpret:1b,nbt:"hud.coins",source:"storage",storage:"ore_tycoon:data"}
scoreboard players set $2 ot.sidebar -2
scoreboard players display name $2 ot.sidebar {type:"text",color:"gray",text:"Per second"}
scoreboard players display numberformat $2 ot.sidebar fixed {type:"nbt",color:"green",interpret:1b,nbt:"hud.per_second",source:"storage",storage:"ore_tycoon:data"}
scoreboard players set $3 ot.sidebar -3
scoreboard players display name $3 ot.sidebar {type:"text",color:"gray",text:"Per click"}
scoreboard players display numberformat $3 ot.sidebar fixed {type:"nbt",color:"aqua",interpret:1b,nbt:"hud.per_click",source:"storage",storage:"ore_tycoon:data"}
scoreboard players set $4 ot.sidebar -4
scoreboard players display name $4 ot.sidebar {type:"text",color:"gray",text:"Lifetime"}
scoreboard players display numberformat $4 ot.sidebar fixed {type:"nbt",color:"yellow",interpret:1b,nbt:"hud.lifetime",source:"storage",storage:"ore_tycoon:data"}
scoreboard players set $5 ot.sidebar -5
scoreboard players display name $5 ot.sidebar ""
scoreboard players set $6 ot.sidebar -6
scoreboard players display name $6 ot.sidebar {type:"text",color:"gray",text:"Level"}
scoreboard players display numberformat $6 ot.sidebar fixed [{type:"score",color:"white",score:{name:"#level",objective:"ot.data"}},{type:"text",color:"dark_gray",text:"/8"}]
execute if score #stars ot.data matches 1.. run function ore_tycoon:generated_scopes/generated_93987b8f
execute unless score #stars ot.data matches 1.. run scoreboard players reset $7 ot.sidebar
execute if score #frenzy ot.data matches 1.. run function ore_tycoon:generated_scopes/generated_fb415e8f
execute unless score #frenzy ot.data matches 1.. run scoreboard players reset $8 ot.sidebar
scoreboard players set $9 ot.sidebar -9
scoreboard players display name $9 ot.sidebar ""
scoreboard players set $10 ot.sidebar -10
scoreboard players display name $10 ot.sidebar {type:"text",color:"dark_gray",text:"Made with Kore"}
scoreboard objectives setdisplay sidebar.team.gold ot.sidebar
function ore_tycoon:hud/refresh
tag @a remove ot.welcomed