package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.client.gui.GuiEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class ClientProxy implements IProxy{

	@Override
	public void openArmorGUI(EntityPlayer player, EntityLiving living) {}

	@Override
	public void openEffectGUI(EntityPlayer player) {
		Minecraft.getInstance().displayGuiScreen(new GuiEffect());
	}

	public static GuiScreen openGui(FMLPlayMessages.OpenContainer msg)
	{
		//if(msg.getId().getPath().equals("armor"))
		//	return new GuiArmor();
		return null;
	}
}
