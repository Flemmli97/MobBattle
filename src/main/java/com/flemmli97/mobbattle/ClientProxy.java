package com.flemmli97.mobbattle;

import java.util.function.Supplier;

import com.flemmli97.mobbattle.client.gui.GuiArmor;
import com.flemmli97.mobbattle.client.gui.GuiEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ClientProxy implements IProxy{

	@Override
	public void openArmorGUI(EntityPlayer player, EntityLiving living) {
		Minecraft.getInstance().displayGuiScreen(new GuiArmor(player.inventory, living));
	}

	@Override
	public void openEffectGUI(EntityPlayer player) {
		Minecraft.getInstance().displayGuiScreen(new GuiEffect());
	}

	@Override
	public EntityPlayer getPlayer(Supplier<Context> ctx) {
		return Minecraft.getInstance().player;
	}
}
