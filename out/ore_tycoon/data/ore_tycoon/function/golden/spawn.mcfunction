kill @e[tag=ot.golden]
execute store result score #random ot.data run random value 0..7
execute if score #random ot.data matches 0 run function ore_tycoon:golden/spot_0
execute if score #random ot.data matches 1 run function ore_tycoon:golden/spot_1
execute if score #random ot.data matches 2 run function ore_tycoon:golden/spot_2
execute if score #random ot.data matches 3 run function ore_tycoon:golden/spot_3
execute if score #random ot.data matches 4 run function ore_tycoon:golden/spot_4
execute if score #random ot.data matches 5 run function ore_tycoon:golden/spot_5
execute if score #random ot.data matches 6 run function ore_tycoon:golden/spot_6
execute if score #random ot.data matches 7 run function ore_tycoon:golden/spot_7
scoreboard players set #golden_life ot.data 15
playsound minecraft:block.amethyst_block.chime master @a[tag=ot.in] ~ ~ ~ 1 1.4
tellraw @a[tag=ot.in] [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"text",color:"yellow",text:"A "},{type:"text",bold:1b,color:"gold",text:"Golden Nugget"},{type:"text",color:"yellow",text:" appeared, grab it quick!"}]