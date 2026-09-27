execute as @a[tag=ot.in] run function ore_tycoon:player/leave
execute in minecraft:overworld run forceload add -20 -20 19 19
schedule function ore_tycoon:admin/uninstall_finish 40 replace
tellraw @a [{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"text",color:"yellow",text:"Uninstalling..."}]