# MobBattle 
[![](http://cf.way2muchnoise.eu/full_268746_Forge_%20.svg)![](http://cf.way2muchnoise.eu/versions/268746.svg)](https://www.curseforge.com/minecraft/mc-mods/mob-battle-mod)  
[![](http://cf.way2muchnoise.eu/full_552316_Fabric_%20.svg)![](http://cf.way2muchnoise.eu/versions/552316.svg)](https://www.curseforge.com/minecraft/mc-mods/mob-battle-mod-fabric)  
[![](https://img.shields.io/modrinth/dt/LbrJcAlI?logo=modrinth&label=Modrinth)![](https://img.shields.io/modrinth/game-versions/LbrJcAlI?logo=modrinth&label=Latest%20for)](https://modrinth.com/mod/mob-battle-mod)  
[![Discord](https://img.shields.io/discord/790631506313478155?color=0a48c4&label=discord)](https://discord.gg/8Cx26tfWNs)

Mob Battle mod for minecraft.

Adds creative utensils for helping creating mob battles

To use this mod as a dependency add the following snippet to your build.gradle:  
```groovy
repositories {
    maven {
        name = "Flemmli97"
        url "https://gitlab.com/api/v4/projects/21830712/packages/maven"
    }
}

dependencies {    
    //Fabric/Loom==========    
    modImplementation("io.github.flemmli97:mobbattle:${minecraft_version}-${mod_version}-${mod_loader}")
    
    //Forge==========    
    compile fg.deobf("io.github.flemmli97:mobbattle:${minecraft_version}-${mod_version}-${mod_loader}")
}
```