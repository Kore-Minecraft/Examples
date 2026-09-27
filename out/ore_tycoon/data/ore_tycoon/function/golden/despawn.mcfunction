execute at @e[tag=ot.golden,type=minecraft:item_display] run particle minecraft:poof ~ ~ ~ 0.3 0.3 0.3 0.02 12
kill @e[tag=ot.golden]
execute store result score #golden_timer ot.data run random value 45..90