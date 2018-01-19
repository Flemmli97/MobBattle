package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class MobEffect extends ItemSword{
	
	public MobEffect()
	{
		super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_effect");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_) {
		return EnumAction.NONE;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
		return HashMultimap.create();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {

		list.add(EnumChatFormatting.AQUA + "Left click an entity to remove their potion effects");
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(entity instanceof EntityLivingBase)
		{
			EntityLiving e = (EntityLiving) entity;
			e.clearActivePotions();
			if (!player.worldObj.isRemote)
			{
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Effects cleared"));
			}
		}
	    return true;
	}
}
