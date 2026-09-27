summon minecraft:text_display ~ ~ ~ {transformation:[0.9f,0.0f,0.0f,0.0f,0.0f,0.9f,0.0f,0.0f,0.0f,0.0f,0.9f,0.0f,0.0f,0.0f,0.0f,1.0f],Tags:["ot.entity","ot.pop","ot.pop_new"],brightness:{block:15,sky:15},billboard:"center",text:[{type:"text",color:"gold",text:"+"},{type:"nbt",bold:1b,color:"gold",interpret:1b,nbt:"hud.gain",source:"storage",storage:"ore_tycoon:data"}],line_width:400,background:0,shadow:1b}
execute if score #level ot.data matches 1 run particle block{block_state:{Name:"minecraft:stone"}} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 2 run particle block{block_state:{Name:"minecraft:coal_ore"}} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 3 run particle block{block_state:{Name:"minecraft:iron_ore"}} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 4 run particle block{block_state:{Name:"minecraft:gold_ore"}} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 5 run particle block{block_state:{Name:"minecraft:diamond_ore"}} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 6 run particle block{block_state:{Name:"minecraft:emerald_ore"}} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 7 run particle block{block_state:{Name:"minecraft:ancient_debris"}} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 8 run particle block{block_state:{Name:"minecraft:beacon"}} ~ ~ ~ 0.2 0.2 0.2 0 6