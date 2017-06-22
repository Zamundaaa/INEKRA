# INEKRA
The final direction of this game project isn't yet clear as it's still in early development, but it'll probably be buliding and tech, probably space, too.
The world is built up from MineCraft like blocks, as those are handled fairly easy and I'm bad at 3D model creation.
Progress and plans so far (not ordered):
  - Automatically generated world with natural terrain and vegetation ~ 10%
    --> Fairly easy terrain with trees is there
    --> Biomes, caves, deeply located cave worlds etc. are to come
  - loading and unloading of chunks with contents with compression ~ 50%
    --> loading and unloading works generally, but just for the blocks. No entities are handled here yet
    --> compression will be redone better very soon
  - Flowing, visually appealing water ~90% (can be made look better)
  - Visually appealing sky ~ 80%
    --> Sky with sun and moon, sunsets etc. Also randomly generated stars
    --> Clouds made out of many, many particles, which look really good sometimes. 
            Probably randomly generated 3D models will come as an (more performant) option, too
  - Easy to handle GUI system which doesn't look too bad ~ 75%
    --> the menu is not ready by far
    --> Blending issue with translucent parts of the GUI on opaque ones. Blends on the background, 
            even if an opaque GUI part is in front of that
  - Inventory system which can handle Items and so on ~ 25%
  - Possability of loading resource packs (user defined ones, too) ~ 50%
    --> language packs, too, although those are easily done using configs
  - Easy config loading and writing system ~90%
  - Multiplayer ~ 10%
    --> main systems for server-client communication work mostly now. 
    --> Entity transmission is on schedule, mostly done for players
    --> login system is on schedule
  - Pathfinding ~ 1%
    --> will be implemented in the not too far future
  - AI for animals and several other entities that will come ~1%
    --> very easy and stupid following system is in place, but isn't really applicable
    --> pathfinding is necessary bevore doing this
    --> good systems on schedule for far future
  - Fairly good particle system ~95%
