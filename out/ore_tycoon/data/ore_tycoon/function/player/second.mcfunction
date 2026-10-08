execute if score #built ot.data matches 1 as @a[tag=ot.waiting] run function ore_tycoon:player/enter
execute unless score #built ot.data matches 1 as @a[tag=ot.in] run function ore_tycoon:player/rescue
execute as @a[tag=ot.in] unless entity @s[dx=40.0,dy=40.0,dz=40.0,x=-20.0,y=263.0,z=-20.0] run function ore_tycoon:player/cleanup
execute as @a[dx=40.0,dy=40.0,dz=40.0,tag=!ot.in,x=-20.0,y=263.0,z=-20.0] run function ore_tycoon:player/join