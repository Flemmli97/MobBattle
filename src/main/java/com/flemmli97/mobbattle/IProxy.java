package com.flemmli97.mobbattle;

import java.util.function.Supplier;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IProxy {

	
	public void openArmorGUI(EntityPlayer player, EntityLiving living);
	
	public void openEffectGUI(EntityPlayer player);

	public EntityPlayer getPlayer(Supplier<NetworkEvent.Context> ctx);
}
