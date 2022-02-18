Mob Battle 2.4.0
================
- Internal changes

Mob Battle 2.3.9
================
- Fix crash with mob equipment item

Mob Battle 2.3.8
================
- Port to 1.18

Mob Battle 2.3.7
================
- Fix server crash

Mob Battle 2.3.6
================
- Fix crash with some items
- Add missing langs for some stuff

Mob Battle 2.3.5
================
- Fix mods.toml version
- Add support for spawneggs from TenshiLib
- Add spawnegg color for missing mobs (iron/snow golem etc.)
- Add support for mobs using the brain ai (piglins, hoglins etc.)
- Fix box renderer
- Change tooltips to use lang files

Mob Battle 2.3.4
================
- Port to 1.16

Mob Battle 2.3.3
================
- Fix evoker team not applying to summoned vexes
- Changed all CreatureEntity references to MobEntity so Ghast, Phantoms etc. are included too. (Slime will attack but wont do dmg due to hardcoded dmg behaviour)
- Remove all targetGoal which also fixes phantoms not attacking properly

Mob Battle 2.3.2
================

- Added MCA support
- Dispenser now use the spawn eggs
- Spawn egg changing now needs creative mode
- Fixed Vanilla-Fix incompability
- Added auto target of mobs from other teams (configurable)
- Fixed nbt with spawnegg being broken
- Particles display for teams is can now be turned off in config
- Fixed #5
