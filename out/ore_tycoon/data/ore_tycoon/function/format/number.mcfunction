execute if score #format.input ot.data matches 1000000000.. run return run function ore_tycoon:format/unit_b
execute if score #format.input ot.data matches 1000000.. run return run function ore_tycoon:format/unit_m
execute if score #format.input ot.data matches 1000.. run return run function ore_tycoon:format/unit_k
execute store result storage ore_tycoon:data args.whole int 1 run scoreboard players get #format.input ot.data
data modify storage ore_tycoon:data args.suffix set value ""
function ore_tycoon:format/plain with storage ore_tycoon:data args