scoreboard players add #clock ot.data 1
execute if score #clock ot.data matches 20.. run scoreboard players set #clock ot.data 0
execute if score #clock ot.data matches 0 run function ore_tycoon:second
execute if score #built ot.data matches 1 run function ore_tycoon:animations