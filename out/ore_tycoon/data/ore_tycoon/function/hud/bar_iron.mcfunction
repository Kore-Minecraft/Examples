bossbar set ore_tycoon:progress color yellow
bossbar set ore_tycoon:progress max 90000
scoreboard players operation #temp ot.data = #lifetime ot.data
scoreboard players operation #temp ot.data -= #10000 ot.data
execute store result bossbar ore_tycoon:progress value run scoreboard players get #temp ot.data
bossbar set ore_tycoon:progress name [{"type":"text","bold":true,"color":"gold","text":"Level 3 "},{"type":"text","color":"dark_gray","text":"» "},{"type":"nbt","color":"yellow","interpret":true,"nbt":"hud.lifetime","source":"storage","storage":"ore_tycoon:data"},{"type":"text","color":"gray","text":" / 100K mined"},{"type":"text","color":"dark_gray","text":" » next core: Gold Ore"}]