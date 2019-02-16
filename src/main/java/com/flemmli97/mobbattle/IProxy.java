package com.flemmli97.mobbattle;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy {

	
	public void openArmorGUI(EntityPlayer player, EntityLiving living);
	
	public void openEffectGUI(EntityPlayer player);

}
