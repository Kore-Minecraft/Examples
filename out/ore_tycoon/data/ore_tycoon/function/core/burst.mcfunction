summon minecraft:text_display ~ ~ ~ {billboard:center,brightness:{block:15,sky:15},transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[0.9f,0.9f,0.9f],translation:[0.0f,0.0f,0.0f]},line_width:400,shadow:1b,Tags:["ot.entity","ot.pop","ot.pop_new"],text:[{type:text,text:""},{type:text,color:gold,text:"+"},{type:nbt,bold:1b,color:gold,interpret:1b,nbt:"hud.gain",source:storage,storage:"ore_tycoon:data"}],background:0}
execute if score #level ot.data matches 1 run particle minecraft:block{block_state:"minecraft:stone"} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 2 run particle minecraft:block{block_state:"minecraft:coal_ore"} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 3 run particle minecraft:block{block_state:"minecraft:iron_ore"} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 4 run particle minecraft:block{block_state:"minecraft:gold_ore"} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 5 run particle minecraft:block{block_state:"minecraft:diamond_ore"} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 6 run particle minecraft:block{block_state:"minecraft:emerald_ore"} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 7 run particle minecraft:block{block_state:"minecraft:ancient_debris"} ~ ~ ~ 0.2 0.2 0.2 0 6
execute if score #level ot.data matches 8 run particle minecraft:block{block_state:"minecraft:beacon"} ~ ~ ~ 0.2 0.2 0.2 0 6