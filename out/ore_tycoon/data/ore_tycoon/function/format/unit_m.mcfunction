scoreboard players operation #format.whole ot.data = #format.input ot.data
scoreboard players operation #format.whole ot.data /= #1000000 ot.data
scoreboard players operation #format.fraction ot.data = #format.input ot.data
scoreboard players operation #format.fraction ot.data %= #1000000 ot.data
data modify storage ore_tycoon:data args.suffix set value "M"
data modify storage ore_tycoon:data args.pad set value ""
execute store result storage ore_tycoon:data args.whole int 1 run scoreboard players get #format.whole ot.data
execute if score #format.whole ot.data matches 100.. run return run function ore_tycoon:format/plain with storage ore_tycoon:data args
scoreboard players operation #format.fraction ot.data /= #10000 ot.data
execute if score #format.whole ot.data matches 10.. run scoreboard players operation #format.fraction ot.data /= #10 ot.data
execute if score #format.whole ot.data matches ..9 if score #format.fraction ot.data matches ..9 run data modify storage ore_tycoon:data args.pad set value "0"
execute store result storage ore_tycoon:data args.fraction int 1 run scoreboard players get #format.fraction ot.data
function ore_tycoon:format/decimal with storage ore_tycoon:data args