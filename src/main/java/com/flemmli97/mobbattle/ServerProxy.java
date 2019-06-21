package com.flemmli97.mobbattle;

import java.util.function.Supplier;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ServerProxy implements IProxy{

	@Override
	public void openArmorGUI(EntityPlayer player, EntityLiving living) {}

	@Override
	public void openEffectGUI(EntityPlayer player) {}

	@Override
	public EntityPlayer getPlayer(Supplier<Context> ctx) {
		return ctx.get().getSender();
	}
}