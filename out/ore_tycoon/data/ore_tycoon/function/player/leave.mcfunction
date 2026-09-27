execute unless entity @s[tag=ot.in] run return run title @s actionbar {type:"text",color:"red",text:"You are not in the factory."}
function ore_tycoon:player/cleanup
execute store result storage ore_tycoon:data args.id int 1 run scoreboard players get @s ot.pid
data remove storage ore_tycoon:data args.return
function ore_tycoon:player/load_return with storage ore_tycoon:data args
execute unless data storage ore_tycoon:data args.return.pos run return run spreadplayers 0 0 0 64 under 239 false @s
data modify storage ore_tycoon:data args.x set from storage ore_tycoon:data args.return.pos[0]
data modify storage ore_tycoon:data args.y set from storage ore_tycoon:data args.return.pos[1]
data modify storage ore_tycoon:data args.z set from storage ore_tycoon:data args.return.pos[2]
data modify storage ore_tycoon:data args.yaw set from storage ore_tycoon:data args.return.rotation[0]
data modify storage ore_tycoon:data args.pitch set from storage ore_tycoon:data args.return.rotation[1]
data modify storage ore_tycoon:data args.dimension set from storage ore_tycoon:data args.return.dimension
function ore_tycoon:player/teleport_back with storage ore_tycoon:data args
title @s actionbar {type:"text",color:"gold",text:"See you soon! Your drills keep mining while you are away."}