execute as @a[tag=ot.in] run function ore_tycoon:player/leave
tag @a remove ot.waiting
execute in minecraft:overworld run forceload add -19 -19 19 19
schedule function ore_tycoon:admin/uninstall_finish 40 replace
tellraw @a [{type:"text",text:""},{type:"text",bold:1b,color:"gold",text:"⛏ Ore Tycoon "},{type:"text",color:"dark_gray",text:"» "},{type:"text",color:"yellow",text:"Uninstalling..."}]