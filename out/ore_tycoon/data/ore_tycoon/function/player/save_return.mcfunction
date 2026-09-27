$data remove storage ore_tycoon:data players[{id:$(id)}]
$data modify storage ore_tycoon:data players append value {id:$(id)}
$data modify storage ore_tycoon:data players[{id:$(id)}].pos set from entity @s Pos
$data modify storage ore_tycoon:data players[{id:$(id)}].rotation set from entity @s Rotation
$data modify storage ore_tycoon:data players[{id:$(id)}].dimension set from entity @s Dimension