package com.flemmli97.mobbattle;

import java.util.function.Supplier;

import com.flemmli97.mobbattle.client.gui.GuiEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ClientProxy implements IProxy{

	@Override
	public void openArmorGUI(PlayerEntity player, MobEntity living) {
		//Minecraft.getInstance().displayGuiScreen(new GuiArmor(player.inventory, living));
	}

	@Override
	public void openEffectGUI(PlayerEntity player) {
		Minecraft.getInstance().displayGuiScreen(new GuiEffect());
	}

	@Override
	public PlayerEntity getPlayer(Supplier<Context> ctx) {
		return Minecraft.getInstance().player;
	}
}
