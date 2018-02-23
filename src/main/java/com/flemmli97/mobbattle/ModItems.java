package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.items.MobEffect;
import com.flemmli97.mobbattle.items.MobGroup;
import com.flemmli97.mobbattle.items.MobHeal;
import com.flemmli97.mobbattle.items.MobKill;
import com.flemmli97.mobbattle.items.MobStick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static ToolMaterial mob_mat = EnumHelper.addToolMaterial("mob_mat", 0, -1, 1.0F, -5.0F, 0);

	public static Item mobStick;
	public static Item mobKill;
	public static Item mobHeal;
	public static Item mobEffect;
	public static Item mobGroup;
	
	public static final void init() {
		
		GameRegistry.registerItem(mobStick = new MobStick(), "mob_stick");
		GameRegistry.registerItem(mobKill = new MobKill(), "mob_kill");
		GameRegistry.registerItem(mobHeal = new MobHeal(), "mob_heal");
		GameRegistry.registerItem(mobEffect = new MobEffect(), "mob_effect");
		GameRegistry.registerItem(mobGroup = new MobGroup(), "mob_group"); 
	}
	

	public static final void initModels()
	{
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mobStick, 0, new ModelResourceLocation(((MobStick) mobStick).getRegistryName(), "inventory"));
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mobKill, 0, new ModelResourceLocation(((MobKill) mobKill).getRegistryName(), "inventory"));
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mobHeal, 0, new ModelResourceLocation(((MobHeal) mobHeal).getRegistryName(), "inventory"));
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mobEffect, 0, new ModelResourceLocation(((MobEffect) mobEffect).getRegistryName(), "inventory"));
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mobGroup, 0, new ModelResourceLocation(((MobGroup) mobGroup).getRegistryName(), "inventory"));
	}
}
