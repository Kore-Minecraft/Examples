scoreboard players set $7 ot.sidebar -7
scoreboard players display name $7 ot.sidebar {type:"text",color:"gray",text:"Stars"}
scoreboard players display numberformat $7 ot.sidebar fixed [{type:"text",color:"light_purple",text:"★ "},{type:"score",color:"light_purple",score:{name:"#stars",objective:"ot.data"}}]