package com.flemmli97.mobbattle;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ServerProxy implements IProxy{
	
	public static final EntityEquipmentSlot slot[] = {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.HEAD
			,EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

	@Override
	public void openArmorGUI(EntityPlayer player, EntityLiving living) {
		
	}

	@Override
	public void openEffectGUI(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}
}