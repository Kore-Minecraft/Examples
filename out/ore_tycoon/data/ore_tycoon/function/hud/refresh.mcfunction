scoreboard players operation #format.input ot.data = #lifetime ot.data
function ore_tycoon:format/number
data modify storage ore_tycoon:data hud.lifetime set from storage ore_tycoon:data hud.out
function ore_tycoon:hud/core_label
function ore_tycoon:hud/labels
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
scoreboard players set $6 ot.sidebar -6
scoreboard players display name $6 ot.sidebar {type:"text",color:"gray",text:"Level"}
scoreboard players display numberformat $6 ot.sidebar fixed [{type:"score",color:"white",score:{name:"#level",objective:"ot.data"}},{type:"text",color:"dark_gray",text:"/8"}]
execute if score #stars ot.data matches 1.. run function ore_tycoon:generated_scopes/generated_93987b8f
execute unless score #stars ot.data matches 1.. run scoreboard players reset $7 ot.sidebar
execute if score #frenzy ot.data matches 1.. run function ore_tycoon:generated_scopes/generated_fb415e8f
execute unless score #frenzy ot.data matches 1.. run scoreboard players reset $8 ot.sidebar
execute if score #level ot.data matches 1 run function ore_tycoon:hud/bar_stone
execute if score #level ot.data matches 2 run function ore_tycoon:hud/bar_coal
execute if score #level ot.data matches 3 run function ore_tycoon:hud/bar_iron
execute if score #level ot.data matches 4 run function ore_tycoon:hud/bar_gold
execute if score #level ot.data matches 5 run function ore_tycoon:hud/bar_diamond
execute if score #level ot.data matches 6 run function ore_tycoon:hud/bar_emerald
execute if score #level ot.data matches 7 run function ore_tycoon:hud/bar_debris
execute if score #level ot.data matches 8 run function ore_tycoon:hud/bar_beacon
bossbar set ore_tycoon:progress players @a[tag=ot.in]