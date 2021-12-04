package io.github.flemmli97.mobbattle.fabric.network;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.resources.ResourceLocation;

public class PacketID {

    public static final ResourceLocation equipMessage = new ResourceLocation(MobBattle.MODID, "equip_message");
    public static final ResourceLocation effectMessage = new ResourceLocation(MobBattle.MODID, "effect_message");
}
