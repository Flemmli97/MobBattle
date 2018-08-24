package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.fatemod.common.utils.SpawnEntityCustomList;
import com.flemmli97.mobbattle.CommonProxy;
import com.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import com.flemmli97.runecraftory.common.init.EntitySpawnEggList;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MultiItemColor implements IItemColor{
	
	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex)
    {
		ResourceLocation id = ItemExtendedSpawnEgg.getNamedIdFrom(stack);
		EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(id);
		if(eggInfo==null && CommonProxy.fate)
			eggInfo = SpawnEntityCustomList.entityEggs.get(id);
		if(eggInfo == null && CommonProxy.runecraftory)
			eggInfo = EntitySpawnEggList.entityEggs.get(id);
        return eggInfo == null ? -1 : (tintIndex == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor);
    }

}
