package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;

public class MobHeal  extends ItemSword{
	
	public MobHeal(ToolMaterial mat)
	{
		super(mat);
		this.setUnlocalizedName("mob_heal");
	    this.setMaxStackSize(1);
	    this.setCreativeTab(MobBattle.customTab);
	    this.setTextureName(MobBattle.MODID + ":mob_heal");
	}

	@Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_) {
		return EnumAction.none;
	}

	@Override
	public Multimap<?, ?> getItemAttributeModifiers() {
		// TODO Auto-generated method stub
		return HashMultimap.create();
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		
		if(entity instanceof EntityLiving)
		{
			((EntityLiving) entity).heal(((EntityLiving) entity).getMaxHealth() - ((EntityLiving) entity).getHealth());
			entity.worldObj.spawnParticle("heart", entity.posX, entity.posY + entity.height + 0.5, entity.posZ, 0, 0.1, 0);
					
		}
		return true;
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
		list.add(EnumChatFormatting.AQUA + "Left click on entity to heal it");
	}
}
