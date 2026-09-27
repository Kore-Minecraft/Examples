execute if score #golden_life ot.data matches 1.. run return run function ore_tycoon:golden/countdown
execute unless entity @a[tag=ot.in] run return 0
scoreboard players remove #golden_timer ot.data 1
execute if score #golden_timer ot.data matches ..0 run function ore_tycoon:golden/spawn