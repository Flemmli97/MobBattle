package com.flemmli97.mobbattle;

import java.util.function.Supplier;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IProxy {

	
	public void openArmorGUI(PlayerEntity player, int windowID, MobEntity living);
	
	public void openEffectGUI(PlayerEntity player);

	public PlayerEntity getPlayer(Supplier<NetworkEvent.Context> ctx);
}
