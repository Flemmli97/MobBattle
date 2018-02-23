package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class MobStick extends ItemSword{
	
	EntityLiving storedEntity = null;
	
	public MobStick(ToolMaterial mat)
	{
		super(mat);
        this.setUnlocalizedName("mob_stick");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        this.setTextureName(MobBattle.MODID + ":mob_stick");
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_) {
		return EnumAction.none;
	}

	@Override
	public Multimap<?, ?> getItemAttributeModifiers() {
		return HashMultimap.create();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {

		if(storedEntity != null)
		{
			list.add(EnumChatFormatting.GREEN + "Asigned entity: " + storedEntity.getClass().getSimpleName());
		}
		list.add(EnumChatFormatting.AQUA + "Left click to asign an entity");
		list.add(EnumChatFormatting.AQUA + "Right click to reset");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!player.worldObj.isRemote)
		if(storedEntity != null)
		{
			storedEntity = null;
			if (!player.worldObj.isRemote)
			{
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Reset entities"));
			}	
		}
		return stack;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(!player.worldObj.isRemote)
		if (storedEntity != null)
		{
			if (entity instanceof EntityLiving && entity != storedEntity)
			{		
				EntityLiving living = (EntityLiving) entity;
				living.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) storedEntity), 0.0F);
				living.setAttackTarget(storedEntity);
				storedEntity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entity), 0.0F);
				storedEntity.setAttackTarget(living);
				storedEntity = null;
				return true;
			}			
		}
		else if (storedEntity == null && entity instanceof EntityLiving)
		{
			storedEntity = (EntityLiving) entity;
			if (!player.worldObj.isRemote)
			{
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "First entity set, hit another entity to set target"));
			}
			return true;
		}
	    return true;
	}
	
	
}
