package com.flemmli97.mobbattle;

import java.util.function.Supplier;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ServerProxy implements IProxy {

    @Override
    public void openArmorGUI(PlayerEntity player, int windowID, MobEntity living) {
    }

    @Override
    public void openEffectGUI(PlayerEntity player) {
    }

    @Override
    public PlayerEntity getPlayer(Supplier<Context> ctx) {
        return ctx.get().getSender();
    }
}
