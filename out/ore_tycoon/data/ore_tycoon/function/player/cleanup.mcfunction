tag @s remove ot.in
team leave @s
effect clear @s minecraft:saturation
execute if entity @s[tag=ot.was_survival] run gamemode survival @s
tag @s remove ot.was_survival